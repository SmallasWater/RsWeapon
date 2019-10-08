package weapon.players.effects;


public class PlayerEffect extends BaseEffect {

    public static final String ICE = "冰冻玩家";

    public static final String FRAME = "引燃玩家";

    public static final String SHIELD = "防护盾";

    public static final String ADD_HEALTH = "吸血";

    public static final String LIGHTNING = "雷击";

    private String bufferName;

    public PlayerEffect(String bufferName,int load,int time){
        this.bufferName = bufferName;
        this.time = load;
        this.cold = time;
    }

    @Override
    public int getCold() {
        return this.cold;
    }

    @Override
    public int getTime() {
        return time;
    }

    @Override
    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public void setCold(int cold) {
        this.cold = cold;
    }

    public String getBufferName() {
        return bufferName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PlayerEffect){
            return (bufferName.equals(((PlayerEffect) obj).bufferName));
        }
        return false;
    }

    @Override
    public PlayerEffect clone() {
        try {
            return (PlayerEffect) super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }

    }
}
