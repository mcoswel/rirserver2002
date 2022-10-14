import classes.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.ArrayList;

public class NetWork {
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(WheelParam.class);
        kryo.register(Player.class);
        kryo.register(Rooms.class);
        kryo.register(Float.class);
        kryo.register(ArrayList.class);
        kryo.register(RoomList.class);
        kryo.register(RodaCon.class);
    }
}
