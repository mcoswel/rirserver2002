import classes.*;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RirServer {
    Server server;
    HashMap<String, Rooms> roomMap = new HashMap<>();

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
                    removePlayerFromRoom(rodaCon);
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
                        removePlayer(roomMap.get(rodaCon.player.getId()), rodaCon);
                    }
                }

                if (rodaCon.player == null) {
                    return;
                }
                if (!rodaCon.roomId.equals("")) {
                    if (!roomMap.containsKey(rodaCon.roomId)) {
                        rodaCon.roomId = "";
                    }
                }

                if (o instanceof Rooms) {
                    Rooms rooms = (Rooms) o;
                    if (!rodaCon.roomId.equals("")) {
                        removePlayer(roomMap.get(rodaCon.roomId), rodaCon);
                    }
                    if (!roomMap.containsKey(rodaCon.player.getId())) {
                        roomMap.put(rooms.getRoomId(), rooms);
                        addPlayer(rooms, rodaCon);
                        server.sendToAllTCP(getAllRooms());
                    }
                }
                if (o instanceof JoinRoom) {
                    JoinRoom joinRoom = (JoinRoom) o;
                    rodaCon.roomId = joinRoom.getRoomId();
                    if (roomMap.containsKey(rodaCon.player.getId())) {
                        roomMap.remove(rodaCon.player.getId());
                    }
                    if (roomMap.containsKey(joinRoom.getRoomId())) {
                        addPlayer(roomMap.get(joinRoom.getRoomId()), rodaCon);
                        server.sendToAllTCP(getAllRooms());
                    }
                }

                if (!rodaCon.roomId.equals("")) {
                    Rooms rooms = roomMap.get(rodaCon.roomId);

                    if (o instanceof ExitRoom) {
                        removePlayer(rooms, rodaCon);
                        rodaCon.roomId = "";
                        server.sendToAllTCP(getAllRooms());
                    }

                    if (o instanceof DeleteRoom) {
                        DeleteRoom deleteRoom = (DeleteRoom) o;
                        roomMap.remove(deleteRoom.getRoomId());
                        if (rodaCon.roomId.equals(deleteRoom.getRoomId())) {
                            rodaCon.roomId = "";
                        }
                        server.sendToAllTCP(getAllRooms());
                    }

                    if (o instanceof GameState) {
                        GameState gameState = (GameState) o;
                        rooms.setGameState(gameState);
                        rooms.getStateMap().put(rodaCon.player, gameState);

                        if (checkState(rooms)) {
                            if (gameState.equals(GameState.SETUPPLAYER)) {
                                sendToRoomPlayer(rooms, rooms);
                            }
                            if (gameState.equals(GameState.STARTNEWROUND)) {
                                sendToRoomPlayer(rooms, new ExecuteStartNewRound());
                            }
                            if (gameState.equals(GameState.SHOWWHEEL)) {
                                sendToRoomPlayer(rooms, new ExecuteShowWheel());
                            }
                            if (gameState.equals(GameState.STOPSPIN)) {
                                sendToRoomPlayer(rooms, new ExecuteWheelResult());
                            }
                            if (gameState.equals(GameState.CHANGETURN)) {
                                sendToRoomPlayer(rooms, new ExecuteChangeTurn());
                            }
                        }
                    }

                    if (o instanceof RequestSetUpPlayer || o instanceof RequestStartNewRound|| o instanceof RequestContinueTurn
                    || o instanceof RequestChangeTurn || o instanceof RequestBankrupt || o instanceof CheckLetter) {
                        sendToRoomPlayer(rooms, o);
                    }

                    if (o instanceof RequestShowWheel) {
                        rooms.setGameState(GameState.SHOWWHEEL);
                        sendToRoomPlayer(rooms, o);
                    }


                    if (o instanceof WheelResult) {
                        rooms.setGameState(GameState.STOPSPIN);
                        sendToRoomPlayer(rooms, o);
                    }


                    if (o instanceof SetGift || o instanceof RemoveTicket || o instanceof RemoveGift || o instanceof RemoveBonus
                    || o instanceof RemoveFreeTurn || o instanceof ApplyImpulse){
                        sendToRoomExceptConnections(rooms, o, rodaCon.getID());
                    }




                }
            }
        });
        server.start();
    }

    private boolean checkState(Rooms rooms) {
        boolean b = true;
        for (Player player : rooms.getPlayerList()) {
            if (!rooms.getStateMap().get(player).equals(rooms.getGameState())) {
                b = false;
            }
        }
        return b;
    }

    private void addPlayer(Rooms rooms, RodaCon rodaCon) {
        rooms.getConnections().add(rodaCon.getID());
        rooms.getPlayerList().add(rodaCon.player);
        rodaCon.roomId = rooms.getRoomId();
        rooms.getStateMap().put(rodaCon.player, GameState.DEFAULT);
    }

    private void removePlayer(Rooms rooms, RodaCon rodaCon) {
        rooms.getConnections().remove(Integer.valueOf(rodaCon.getID()));
        rooms.getPlayerList().remove(rodaCon.player);
        rodaCon.roomId = "";
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
                roomMap.remove(s);
            }
            server.sendToAllTCP(getAllRooms());
        }
    }

    private void removePlayerFromRoom(RodaCon rodaCon) {
        for (Map.Entry<String, Rooms> entry : roomMap.entrySet()) {
            Rooms rooms = entry.getValue();
            Player toRemove = null;
            for (Player p : rooms.getPlayerList()) {
                if (rodaCon.player.getId().equals(p.getId())) {
                    toRemove = p;
                }
            }
            if (toRemove != null) {
                removePlayer(rooms, rodaCon);
                break;
            }
        }
    }


    private void sendToRoomPlayer(Rooms rooms, Object o) {
        for (Integer r : rooms.getConnections()) {
            server.sendToTCP(r, o);
        }
    }

    private void sendToRoomExceptConnections(Rooms rooms, Object o, int exception) {
        for (Integer r : rooms.getConnections()) {
            if (r != exception) {
                server.sendToTCP(r, o);
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
