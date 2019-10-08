package weapon.task.async;


import cn.nukkit.scheduler.AsyncTask;
import weapon.players.PlayerEffects;
import weapon.players.effects.BaseEffect;

import java.util.LinkedList;

public class SkillCoolingTimer extends AsyncTask {
    private PlayerEffects effects;

    public SkillCoolingTimer(PlayerEffects effects){
        this.effects = effects;
    }


    @Override
    public void onRun() {
        LinkedList<BaseEffect> playerEffects = effects.getEffects();
        if(playerEffects != null && playerEffects.size() > 0){
            try {
                for (BaseEffect effect:playerEffects){
                    if(effect.getCold() > 0){
                        effect.setCold(effect.getCold() - 1);
                    }else{
                        effects.removeEffect(effect);
                    }
                }
            }catch (Exception e){
                effects.setEffects(playerEffects);
            }

        }
    }
}
