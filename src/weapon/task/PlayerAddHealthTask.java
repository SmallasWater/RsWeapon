package weapon.task;

import AwakenSystem.data.baseAPI;
import AwakenSystem.data.defaultAPI;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import healthapi.PlayerHealth;
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
                int health = PlayerAddAttributes.getHealth(player);
                if(Server.getInstance().getPluginManager().getPlugin("HealthAPI") != null){
                    PlayerHealth health1 = PlayerHealth.getPlayerHealth(player);
                    int h2 = health1.getOwnerMaxHealth("Weapon");
                    if(h2 != health){
                        RsWeapon.addHealth.put(player.getName(), health);
                        health1.setMaxHealth("Weapon",health);
                        player.attack(0.1F);
                    }
                    return;
                }
                if(health > 0){
                    if (Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null) {
                        if (this.health == 0) {
                            this.health = health;
                            RsWeapon.addHealth.put(player.getName(), health);
                            defaultAPI.addPlayerAttributeInt(player.getName(), baseAPI.PlayerAttType.HEALTH, (health));
                            player.attack(0.1F);
                        } else if (this.health != health) {
                            RsWeapon.addHealth.put(player.getName(), health);
                            defaultAPI.removePlayerAttributeInt(player.getName(), baseAPI.PlayerAttType.HEALTH, (this.health));
                            defaultAPI.addPlayerAttributeInt(player.getName(), baseAPI.PlayerAttType.HEALTH, (health));
                            this.health = health;
                            player.attack(0.1F);
                        } else {
                            RsWeapon.addHealth.put(player.getName(), health);
                        }
                    } else {
                        if (player.getMaxHealth() != RsWeapon.playerHealth.get(player.getName()) + health) {
                            player.setMaxHealth(RsWeapon.playerHealth.get(player.getName()) + health);
                        }
                    }


                }else{
                    if (Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null) {
                        if (RsWeapon.addHealth.containsKey(player.getName())) {
                            this.health = health;
                            defaultAPI.removePlayerAttributeInt(player.getName(),
                                    baseAPI.PlayerAttType.HEALTH, RsWeapon.addHealth.get(player.getName()));
                            RsWeapon.addHealth.remove(player.getName());
                            player.attack(0.1F);
                        }
                    } else {
                        if (player.getMaxHealth() != RsWeapon.playerHealth.get(player.getName()) + health) {
                            player.setMaxHealth(RsWeapon.playerHealth.get(player.getName()) + health);
                        }
                    }
                }
            }else{
                if(Server.getInstance().getPluginManager().getPlugin("HealthAPI") != null){
                    this.cancel();
                    return;
                }
                if (Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null) {
                        defaultAPI.removePlayerAttributeInt(player.getName(), baseAPI.PlayerAttType.HEALTH,RsWeapon.addHealth.get(player.getName()));
                        RsWeapon.addHealth.remove(player.getName());
                    }
                this.cancel();
            }

        }catch (Exception e){
            if(Server.getInstance().getPluginManager().getPlugin("HealthAPI") != null){
                RsWeapon.addHealth.remove(player.getName());
                this.cancel();
                return;
            }
            String name = player.getName();
            if (Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null) {
                int health = 0;
                if(RsWeapon.addHealth.containsKey(player.getName())){
                    health = RsWeapon.addHealth.get(player.getName());
                }
                if(health > 0){
                    defaultAPI.removePlayerAttributeInt(player.getName(), baseAPI.PlayerAttType.HEALTH,health);
                    RsWeapon.addHealth.remove(name);
                    if(player.isOnline()){
                        player.attack(0.1F);
                    }else{
                        this.cancel();
                    }
                }else{
                    if(!player.isOnline()){
                        this.cancel();
                    }
                }
            }
        }
    }
}
