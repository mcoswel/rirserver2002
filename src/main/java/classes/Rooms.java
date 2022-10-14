package classes;

import java.util.List;
public class Rooms {
    private List<RodaCon> rodaConList;
    private String roomId;
    private String roomName;

    public List<RodaCon> getRodaConList() {
        return rodaConList;
    }

    public void setRodaConList(List<RodaCon> rodaConList) {
        this.rodaConList = rodaConList;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}