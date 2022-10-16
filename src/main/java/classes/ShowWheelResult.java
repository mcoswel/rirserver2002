package classes;


public class ShowWheelResult {
    private float angle;
    private WheelParam wheelParam;
    private boolean bonus;
    private boolean gift;
    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public WheelParam getWheelParam() {
        return wheelParam;
    }

    public void setWheelParam(WheelParam wheelParam) {
        this.wheelParam = wheelParam;
    }

    public boolean isBonus() {
        return bonus;
    }

    public void setBonus(boolean bonus) {
        this.bonus = bonus;
    }

    public boolean isGift() {
        return gift;
    }

    public void setGift(boolean gift) {
        this.gift = gift;
    }
}
