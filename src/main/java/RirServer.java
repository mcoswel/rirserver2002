import classes.*;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static classes.PlayerStates.*;

public class RirServer {
    Server server;
    HashMap<String, Rooms> roomMap = new HashMap<>();
    HashMap<Rooms, StatesCount> stateMap = new HashMap<>();

    public RirServer() throws IOException {
        server = new Server() {
            @Override
            protected Connection newConnection() {
                return new RodaCon();
            }
        };

        NetWork.register(server);
        server.bind(3353);
        server.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                System.out.println("connection id " + connection.getID());
            }

            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
                RodaCon rodaCon = (RodaCon) connection;
                if (rodaCon.player != null) {
                    removePlayerFromRoom(rodaCon.player);
                }
                checkEmptyRoom();
            }

            @Override
            public void received(Connection connection, Object o) {
                super.received(connection, o);
                RodaCon rodaCon = (RodaCon) connection;


                if (o instanceof Player) {
                    Player player = (Player) o;
                    rodaCon.player = player;
                    rodaCon.roomId = "";
                    //remove created room if any during registration
                    server.sendToTCP(rodaCon.getID(), getAllRooms());
                    if (roomMap.containsKey(rodaCon.player.getId())) {
                        roomMap.remove(rodaCon.player.getId());
                    }
                }

                if (rodaCon.player == null) {
                    return;
                }

                if (o instanceof Rooms) {
                    Rooms rooms = (Rooms) o;
                    if (!roomMap.containsKey(rodaCon.player.getId())) {
                        roomMap.put(rooms.getRoomId(), rooms);
                        addPlayer(rooms, rodaCon);
                        server.sendToAllTCP(getAllRooms());

                    }
                }
                if (o instanceof JoinRoom) {
                    JoinRoom joinRoom = (JoinRoom) o;
                    rodaCon.roomId = joinRoom.getRoomId();
                    if (roomMap.containsKey(joinRoom.getRoomId())) {
                        addPlayer(roomMap.get(joinRoom.getRoomId()), rodaCon);
                        server.sendToAllTCP(getAllRooms());
                    }
                }

                if (!rodaCon.roomId.equals("")) {

                    Rooms rooms = roomMap.get(rodaCon.roomId);
                    if (o instanceof DeleteRoom) {
                        DeleteRoom deleteRoom = (DeleteRoom) o;
                        roomMap.remove(deleteRoom.getRoomId());
                        if (rodaCon.roomId.equals(deleteRoom.getRoomId())) {
                            rodaCon.roomId = "";
                        }
                        server.sendToAllTCP(getAllRooms());

                    }

                    if (o instanceof SetGift){
                        sendToRoomPlayer(rooms, o);
                    }

                    if (o instanceof ExitRoom) {
                        rooms.getPlayerList().remove(rodaCon.player);
                        rooms.getConnections().remove(rodaCon.getID());
                        rodaCon.roomId = "";
                        server.sendToAllTCP(getAllRooms());
                    }

                    if (o instanceof RequestStartGame) {
                        StatesCount statesCount = new StatesCount(STARTGAME, rooms.getPlayerList().size());
                        stateMap.put(rooms, statesCount);
                        sendToRoomPlayer(rooms, new RequestStartGame());
                    }

                    if (o instanceof RequestSpin){
                        stateMap.get(rooms).setState(SPINWHEEL);
                        sendToRoomPlayer(rooms, o);
                    }
                    if (o instanceof ApplyImpulse){
                        sendToRoomPlayer(rooms,o);
                    }
                    if (o instanceof ShowWheelResult){
                        stateMap.get(rooms).setState(GETRESULT);
                        sendToRoomPlayer(rooms,o);
                    }

                    if (o instanceof CheckLetter){
                        sendToRoomPlayer(rooms,o);
                    }
                    if (o instanceof LetterResult){
                        LetterResult letterResult = (LetterResult) o;
                        if (letterResult.isCorrect()){
                            sendToRoomPlayer(rooms, new ContinueTurn());
                        }else{
                            sendToRoomPlayer(rooms, new ChangeTurn());
                        }
                    }

                    if (o instanceof RequestShowVocal){
                        stateMap.get(rooms).setState(SHOWVOCAL);
                        sendToRoomPlayer(rooms, o);
                    }

                    if (o instanceof RequestChangeTurn){
                        stateMap.get(rooms).setState(CHANGETURN);
                        sendToRoomPlayer(rooms, o);
                    }

                    if (o instanceof RequestBankrupt){
                        stateMap.get(rooms).setState(CHANGETURN);
                        sendToRoomPlayer(rooms, o);
                        sendToRoomPlayer(rooms, new RequestChangeTurn());
                    }

                    if (o instanceof RequestRoundEnd){
                        stateMap.get(rooms).setState(ROUNDEND);
                        sendToRoomPlayer(rooms, o);
                    }

                    if (o instanceof PlayerStates) {
                        PlayerStates playerStates = (PlayerStates) o;
                        checkStates(rooms, playerStates);
                    }

                }
            }
        });
        server.start();
    }

    private void checkStates(Rooms rooms, PlayerStates playerStates) {
        StatesCount statesCount = stateMap.get(rooms);
        if (playerStates.equals(statesCount.getPlayerStates())) {
            if (statesCount.addCount()) {
                if (statesCount.getPlayerStates().equals(STARTGAME)) {
                    SetPlayer setPlayer = new SetPlayer();
                    setPlayer.setPlayerList(rooms.getPlayerList());
                    sendToRoomPlayer(rooms, setPlayer);
                    statesCount.setState(SETPLAYER);
                } else if (statesCount.getPlayerStates().equals(SETPLAYER)) {
                    statesCount.setState(STARTNEWROUND);
                    sendToRoomPlayer(rooms, new RequestStartNewRound());
                } else if (statesCount.getPlayerStates().equals(STARTNEWROUND)) {
                    sendToRoomPlayer(rooms, new StartNewRound());
                } else if (statesCount.getPlayerStates().equals(SPINWHEEL)) {
                    sendToRoomPlayer(rooms, new ShowWheel());
                    statesCount.setState(READYTOSPIN);
                }else if (statesCount.getPlayerStates().equals(READYTOSPIN)) {
                    sendToRoomPlayer(rooms, new ReadyToSpin());
                }  else if (statesCount.getPlayerStates().equals(GETRESULT)) {
                    sendToRoomPlayer(rooms, new ExecuteWheelResults());
                } else if (statesCount.getPlayerStates().equals(CONTINUETURN)) {
                    sendToRoomPlayer(rooms, new ContinueTurn());
                } else if (statesCount.getPlayerStates().equals(CHANGETURN)) {
                    sendToRoomPlayer(rooms, new ChangeTurn());
                }else if (statesCount.getPlayerStates().equals(SHOWVOCAL)) {
                    sendToRoomPlayer(rooms, new ShowVocal());
                }else if (statesCount.getPlayerStates().equals(ROUNDEND)) {
                    sendToRoomPlayer(rooms, new RoundEnd());
                }
            }
        }
    }

    private void addPlayer(Rooms rooms, RodaCon rodaCon) {
        rooms.getConnections().add(rodaCon.getID());
        rooms.getPlayerList().add(rodaCon.player);
        rodaCon.roomId = rooms.getRoomId();
    }

    private void checkEmptyRoom() {
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, Rooms> entry : roomMap.entrySet()) {
            Rooms rooms = entry.getValue();
            if (rooms.getPlayerList().isEmpty()) {
                toRemove.add(entry.getKey());
            }
        }

        if (!toRemove.isEmpty()) {
            for (String s : toRemove) {
                stateMap.remove(roomMap.get(s));
                roomMap.remove(s);
            }
            server.sendToAllTCP(getAllRooms());
        }
    }

    private void removePlayerFromRoom(Player player) {
        for (Map.Entry<String, Rooms> entry : roomMap.entrySet()) {
            Rooms rooms = entry.getValue();
            Player toRemove = null;
            for (Player p : rooms.getPlayerList()) {
                if (player.getId().equals(p.getId())) {
                    toRemove = p;
                }
            }
            if (toRemove != null) {
                rooms.getPlayerList().remove(toRemove);
                stateMap.get(rooms).setSize(rooms.getPlayerList().size());
                break;
            }
        }
    }


    private void sendToRoomPlayer(Rooms rooms, Object o) {
        for (Connection connection : server.getConnections()) {
            RodaCon rodaCon = (RodaCon) connection;
            if (rodaCon.roomId != null) {
                if (rodaCon.roomId.equals(rooms.getRoomId())) {
                    server.sendToTCP(rodaCon.getID(), o);
                }
            }
        }
    }

    private RoomList getAllRooms() {
        List<Rooms> roomSessionList = new ArrayList<>();
        for (Rooms r : roomMap.values()) {
            roomSessionList.add(r);
        }
        RoomList roomLists = new RoomList();
        roomLists.setRoomsList(roomSessionList);
        return roomLists;
    }

    public static void main(String[] args) throws IOException {
        RirServer rirServer = new RirServer();
    }
}
