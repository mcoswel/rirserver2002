import classes.Player;
import classes.RodaCon;
import classes.RoomList;
import classes.Rooms;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RirServer {
    Server server;
    HashMap<String, Rooms> roomMap = new HashMap<>();
    public RirServer() throws IOException {
        server = new Server(){
            @Override
            protected Connection newConnection() {
                return new RodaCon();
            }
        };

        NetWork.register(server);
        server.bind(3353);
        server.addListener(new Listener(){

            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                server.sendToTCP(connection.getID(),getAllRooms());
            }

            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);

            }

            @Override
            public void received(Connection connection, Object o) {
                super.received(connection, o);
                RodaCon rodaCon = (RodaCon)connection;

                if (o instanceof Player){
                    Player player = (Player) o;
                    rodaCon.player = player;
                    //remove created room if any during registration
                    if (roomMap.containsKey(rodaCon.player.getId())) {
                        roomMap.remove(rodaCon.player.getId());
                    }
                }

                if (o instanceof RoomList){
                    server.sendToAllTCP(o);
                }

                if (o instanceof Rooms){
                    Rooms rooms = (Rooms)o;
                    rooms.getRodaConList().add(rodaCon);
                    roomMap.put(rooms.getRoomId(), rooms);
                }

                if (rodaCon.roomId!=null){

                }
            }
        });
        server.start();
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
