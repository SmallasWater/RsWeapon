package weapon.players.effects;


import cn.nukkit.potion.Effect;
import weapon.items.BaseItem;

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
    public String getBufferName() {
        return BaseItem.getEffectStringById((effect.getId()))
                        +" "+BaseItem.getLevelByString(effect.getAmplifier());
    }

    @Override
    public boolean equals(Object effect) {
        if(effect instanceof MineCraftEffect){
            return (this.effect.getId() == ((MineCraftEffect) effect).effect.getId());
        }
        return false;
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
