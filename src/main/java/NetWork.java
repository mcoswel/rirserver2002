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
        kryo.register(RequestContinueTurn.class);
        kryo.register(RequestShowWheel.class);
        kryo.register(ExecuteShowWheel.class);
        kryo.register(ApplyImpulse.class);
        kryo.register(WheelResult.class);
        kryo.register(ExecuteWheelResult.class);
        kryo.register(RemoveFreeTurn.class);
        kryo.register(ExecuteChangeTurn.class);
        kryo.register(RequestChangeTurn.class);
        kryo.register(RequestBankrupt.class);
        kryo.register(CheckLetter.class);
        kryo.register(SetGift.class);
        kryo.register(RemoveGift.class);
        kryo.register(RemoveBonus.class);
        kryo.register(RemoveTicket.class);
        kryo.register(ShowVocal.class);
    }
}

