import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class NetWork {
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

    }
}
