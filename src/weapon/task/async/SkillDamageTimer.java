package weapon.task.async;

import cn.nukkit.scheduler.AsyncTask;
import weapon.players.PlayerEffects;
import weapon.players.effects.BaseEffect;
import weapon.players.effects.PlayerEffect;

import java.util.LinkedList;

public class SkillDamageTimer extends AsyncTask {

    private PlayerEffects effects;

    public SkillDamageTimer(PlayerEffects effects){
        this.effects = effects;
    }

    @Override
    public void onRun() {
        LinkedList<BaseEffect> playerEffect = effects.getEffects();
        if(playerEffect != null && playerEffect.size() > 0){
            try {
                for (BaseEffect effect : playerEffect) {
                    if (effect instanceof PlayerEffect) {
                        if (((PlayerEffect) effect).getBufferName().equals(PlayerEffect.ICE)) {
                            if (effect.getTime() > 0) {
                                effect.setCold(effect.getCold() - 1);
                            } else {
                                effects.removeEffect(effect);
                            }
                        }
                    }
                }
            }catch (Exception e){
                effects.setEffects(playerEffect);
            }
        }
    }
}
