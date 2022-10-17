import classes.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;

public class NetWork {
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(WheelParam.class);
        kryo.register(Player.class);
        kryo.register(Rooms.class);
        kryo.register(Float.class);
        kryo.register(ArrayList.class);
        kryo.register(RoomList.class);
        kryo.register(Question.class);
        kryo.register(JoinRoom.class);
        kryo.register(ExitRoom.class);
        kryo.register(DeleteRoom.class);
        kryo.register(RequestSetUpPlayer.class);
        kryo.register(AbstractMap.class);
        kryo.register(HashMap.class);
        kryo.register(GameState.class);
        kryo.register(RequestStartNewRound.class);
        kryo.register(ExecuteStartNewRound.class);
    }
}

