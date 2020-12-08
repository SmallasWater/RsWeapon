package weapon.task;

import cn.nukkit.Player;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import weapon.players.effects.PlayerEffects;
import weapon.players.effects.BaseEffect;
import weapon.players.effects.MineCraftEffect;
import weapon.utils.PlayerAddAttributes;

import java.util.LinkedList;

public class PlayerAddEffectTask extends Task {

    private Player player;


    public PlayerAddEffectTask(Player player){
        this.player = player;
    }
    @Override
    public void onRun(int i) {
        if(player.isOnline()){
            PlayerEffects playerEffects = PlayerEffects.getInstance(player.getName());
            LinkedList<BaseEffect> effects = PlayerAddAttributes.getEffects(player);
            if(effects.size() > 0){
                for(BaseEffect effect:effects){
                    if(effect instanceof MineCraftEffect){
                        if(playerEffects.containsEffect(effect)){
                            playerEffects.addEffect(((MineCraftEffect) effect).clone());
                            Effect effect1 = ((MineCraftEffect) effect).getEffect();
                            effect1.setDuration(effect.getTime() * 20);
                            player.addEffect(effect1);
                        }
                    }
                }
            }
        }else{
            this.cancel();
        }
    }
}
