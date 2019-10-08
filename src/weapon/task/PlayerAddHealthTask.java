package weapon.task;

import AwakenSystem.data.baseAPI;
import AwakenSystem.data.defaultAPI;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import weapon.RsWeapon;
import weapon.utils.PlayerAddAttributes;

public class PlayerAddHealthTask extends Task {

    private Player player;

    private int health = 0;

    public PlayerAddHealthTask(Player player){
        this.player = player;
    }


    @Override
    public void onRun(int i) {
        try {
            if(player.isOnline()){
                if(RsWeapon.playerHealth.containsKey(player)){
                    int health = PlayerAddAttributes.getHealth((Player) player);
                    if(health > 0){
                        if (Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null) {
                            if(this.health == 0){
                                this.health = health;
                                RsWeapon.addHealth.put(player,health);
                                defaultAPI.addPlayerAttributeInt(player.getName(), baseAPI.PlayerAttType.HEALTH,(health));
                                player.attack(0.1F);
                            }else if(this.health != health){
                                RsWeapon.addHealth.put(player,health);
                                defaultAPI.removePlayerAttributeInt(player.getName(), baseAPI.PlayerAttType.HEALTH,(this.health));
                                defaultAPI.addPlayerAttributeInt(player.getName(), baseAPI.PlayerAttType.HEALTH,(health));
                                this.health = health;
                                player.attack(0.1F);
                            }else{
                                RsWeapon.addHealth.put(player,health);
                            }
                        } else {
                            if(player.getMaxHealth() != RsWeapon.playerHealth.get(player)+ health){
                                player.setMaxHealth(RsWeapon.playerHealth.get(player)+ health);
                            }
                        }
                    }else{
                        if(Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null){
                            if(RsWeapon.addHealth.containsKey(player)){
                                this.health = health;
                                defaultAPI.removePlayerAttributeInt(player.getName(),
                                        baseAPI.PlayerAttType.HEALTH,RsWeapon.addHealth.get(player));
                                RsWeapon.addHealth.remove(player);
                                player.attack(0.1F);
                            }
                        }else{
                            if(player.getMaxHealth() != RsWeapon.playerHealth.get(player)+ health){
                                player.setMaxHealth(RsWeapon.playerHealth.get(player)+ health);
                            }
                        }
                    }
                }
            }else{
                if (Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null) {
                        RsWeapon.addHealth.remove(player, health);
                        RsWeapon.playerHealth.remove(player);
                        defaultAPI.removePlayerAttributeInt(player.getName(), baseAPI.PlayerAttType.HEALTH,(this.health));
                    }
                this.cancel();
            }

        }catch (Exception ignored){ }
    }
}
