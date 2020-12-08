package weapon.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import weapon.RsWeapon;
import weapon.players.effects.PlayerEffects;
import weapon.task.async.SkillCoolingTimer;
import weapon.utils.PlayerAddAttributes;


public class ForeachPlayersTask extends Task {


    @Override
    public void onRun(int i) {
        for (Player player:Server.getInstance().getOnlinePlayers().values()){
            if(RsWeapon.getInstance().getConfig().getBoolean("是否显示底部",true)){
                if(PlayerAddAttributes.getSuits(player).size() > 0){
                    player.sendActionBar("已激活套装: "+PlayerAddAttributes.getSuits(player).toString()+"\n\n\n");
                }
            }
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
                            new SkillCoolingTimer(effects,true));
                }
            }
        }
    }
}
