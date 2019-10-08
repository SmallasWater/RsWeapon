package weapon.players.effects;


import cn.nukkit.potion.Effect;

public class MineCraftEffect extends BaseEffect{

    private Effect effect;



    public MineCraftEffect(Effect buffer,int cold) {
        this.effect = buffer;
        this.cold = cold;
        this.time = effect.getDuration() / 20;
    }

    public Effect getEffect() {
        return effect;
    }

    @Override
    public boolean equals(Object effect) {
        if(effect instanceof MineCraftEffect){
            return (this.effect.getId() == ((MineCraftEffect) effect).effect.getId()
                    && this.effect.getAmplifier() == ((MineCraftEffect) effect).effect.getAmplifier());
        }
        return false;
    }

    @Override
    public int getCold() {
        return this.cold;
    }

    @Override
    public int getTime() {
        return this.time;
    }

    @Override
    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public void setCold(int cold) {
        this.cold = cold;

    }

    @Override
    public MineCraftEffect clone() {
        try {
            return (MineCraftEffect) super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }

    }
}
