package weapon.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import weapon.RsWeapon;
import weapon.players.PlayerEffects;
import weapon.task.async.SkillCoolingTimer;
import weapon.task.async.SkillDamageTimer;

public class ForeachPlayersTask extends Task {


    @Override
    public void onRun(int i) {
        for (Player player:Server.getInstance().getOnlinePlayers().values()){
            if(RsWeapon.effects.containsKey(player.getName())){
                PlayerEffects effects = RsWeapon.effects.get(player.getName());
                if(effects.getEffects().size() > 0){
                    Server.getInstance().getScheduler().scheduleAsyncTask(RsWeapon.getInstance(),new SkillCoolingTimer(effects));
                }
            }
            if(RsWeapon.damages.containsKey(player.getName())){
                PlayerEffects effects = RsWeapon.damages.get(player.getName());
                if(effects.getEffects().size() > 0){
                    Server.getInstance().getScheduler().scheduleAsyncTask(RsWeapon.getInstance(),
                            new SkillDamageTimer(effects));
                }
            }
            if(player.isOnline()){
                Server.getInstance().getScheduler().scheduleDelayedTask(new PlayerAddEffectTask(player),2);
            }
        }
    }
}
