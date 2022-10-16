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
        kryo.register(JoinRoom.class);
        kryo.register(DeleteRoom.class);
        kryo.register(ExitRoom.class);
        kryo.register(Question.class);
        kryo.register(SetPlayer.class);
        kryo.register(PlayerStates.class);
        kryo.register(StartNewRound.class);
        kryo.register(ShowWheel.class);
        kryo.register(RequestSpin.class);
        kryo.register(ReadyToSpin.class);
        kryo.register(ShowWheelResult.class);
        kryo.register(ApplyImpulse.class);
        kryo.register(ExecuteWheelResults.class);
        kryo.register(CheckLetter.class);
        kryo.register(ContinueTurn.class);
        kryo.register(ChangeTurn.class);
        kryo.register(LetterResult.class);
        kryo.register(ShowVocal.class);
        kryo.register(RequestStartGame.class);
        kryo.register(RequestStartNewRound.class);
        kryo.register(RequestShowVocal.class);
        kryo.register(SetGift.class);
        kryo.register(RequestChangeTurn.class);
        kryo.register(RequestRoundEnd.class);
        kryo.register(RoundEnd.class);
        kryo.register(RequestBankrupt.class);
    }
}

