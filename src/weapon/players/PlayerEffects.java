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
            effects.remove(effect);
        }
        effects.add(effect);
    }


    public void removeEffect(BaseEffect effect){
        effects.remove(effect);
    }


    public boolean containsEffect(BaseEffect effect){
        for(BaseEffect baseEffect:effects){
            if(baseEffect.equals(effect)){
                return true;
            }
        }
        return false;
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

}
