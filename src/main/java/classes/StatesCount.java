package classes;

public class StatesCount {
    PlayerStates playerStates;
    int count;
    int size;

    public StatesCount(PlayerStates playerStates, int size) {
        this.playerStates = playerStates;
        this.size = size;
    }

    public boolean addCount(){
        count++;
        if (count>=size){
            return true;
        }
        return false;
    }

    public void setState(PlayerStates playerStates){
        this.playerStates = playerStates;
        count = 0;
    }

    public PlayerStates getPlayerStates() {
        return playerStates;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
