package weapon.players;

import weapon.RsWeapon;
import weapon.players.effects.BaseEffect;
import weapon.players.effects.PlayerEffect;


import java.util.LinkedList;

/**
 * @author 若水
 * 本类为玩家的状态类
 * 正面效果 与 负面效果
 * */
public class PlayerEffects {

    private String player;

    private LinkedList<BaseEffect> effects = new LinkedList<>();

    private PlayerEffects(String player){
        this.player = player;
    }

    public static PlayerEffects getInstance(String player){
        if(!RsWeapon.effects.containsKey(player)){
            RsWeapon.effects.put(player,new PlayerEffects(player));
        }
        return RsWeapon.effects.get(player);
    }

    public void addEffect(BaseEffect effect){
        if(containsEffect(effect)){
            effects.add(effect);
        }
    }


    public void removeEffect(BaseEffect effect){
        effects.remove(effect);
    }


    /**
     * 判断是否不存在药水
     * */
    public boolean containsEffect(BaseEffect effect){
        if(effect != null){
            if(effects != null){
                try {
                    for(BaseEffect baseEffect:effects){
                        if(baseEffect.equals(effect)){
                            return false;
                        }
                    }
                }catch (Exception e){
                    return true;
                }
            }
        }
        return true;
    }

    BaseEffect containsEffect(){
        for(BaseEffect baseEffect:effects){
            if(baseEffect instanceof PlayerEffect){
                if(((PlayerEffect) baseEffect).getBufferName().equals(PlayerEffect.ICE)){
                    return baseEffect;
                }
            }
        }
        return null;
    }


    public LinkedList<BaseEffect> getEffects() {
        return effects;
    }

    public void setEffects(LinkedList<BaseEffect> effects) {
        this.effects = effects;
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(BaseEffect effect:effects){
            builder.append("Buffer")
                    .append(effect.getClass().getName())
                    .append(" load: ")
                    .append(effect.getTime())
                    .append(" cold: ")
                    .append(effect.getTime());
        }
        return builder.toString();
    }
}
