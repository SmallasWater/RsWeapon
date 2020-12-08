package weapon.task.async;


import cn.nukkit.scheduler.AsyncTask;
import weapon.players.effects.PlayerEffects;
import weapon.players.effects.BaseEffect;

import java.util.LinkedList;

public class SkillCoolingTimer extends AsyncTask {

    private PlayerEffects effects;

    private boolean canLoad = false;

    public SkillCoolingTimer(PlayerEffects effects){
        this.effects = effects;
    }

    public SkillCoolingTimer(PlayerEffects effects,boolean canLoad){
        this.effects = effects;
        this.canLoad = canLoad;
    }


    @Override
    public void onRun() {
        LinkedList<BaseEffect> playerEffects = effects.getEffects();
        if(playerEffects != null && playerEffects.size() > 0){
            try {
                for (BaseEffect effect:playerEffects){
                    if(!canLoad){
                        if(effect.getCold() > 0){
                            effect.setCold(effect.getCold() - 1);
                        }else{
                            effects.removeEffect(effect);
                        }
                    }else{
                        if (effect.getTime() > 0) {
                            effect.setTime(effect.getTime() - 1);
                        } else {
                            effects.removeEffect(effect);
                        }
                    }
                }
            }catch (Exception e){
                effects.setEffects(playerEffects);
            }
        }
    }
}
