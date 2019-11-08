package weapon.players;

import AwakenSystem.data.baseAPI;
import AwakenSystem.data.defaultAPI;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import weapon.RsWeapon;
import weapon.items.Armor;
import weapon.items.Weapon;
import weapon.players.effects.BaseEffect;
import weapon.players.effects.MineCraftEffect;
import weapon.players.effects.PlayerEffect;
import weapon.players.effects.PlayerEffects;
import weapon.task.PlayerAddEffectTask;
import weapon.task.PlayerAddHealthTask;
import weapon.utils.Effects;
import weapon.utils.PlayerAddAttributes;
import weapon.utils.RsWeaponSkill;
import weapon.utils.Skill;


import java.util.LinkedList;

public class OnListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        RsWeapon.playerHealth.put(player.getName(), player.getMaxHealth());
        Server.getInstance().getScheduler().scheduleRepeatingTask(new PlayerAddHealthTask(player),20);
        Server.getInstance().getScheduler().scheduleRepeatingTask(new PlayerAddEffectTask(player),20);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event){
        if(event instanceof EntityDamageByEntityEvent){
            Entity entity = event.getEntity();
            Entity damagePlayer = ((EntityDamageByEntityEvent) event).getDamager();
            if(damagePlayer == null || entity == null){
                return;
            }
            if(damagePlayer instanceof Player){
                Item item = ((Player) damagePlayer).getInventory().getItemInHand();
                if(Weapon.isWeapon(item)){
                    float damage = event.getDamage();
                    LinkedList<BaseEffect> damageEffects = PlayerAddAttributes.getDamages((Player) damagePlayer);
                    damage += PlayerAddAttributes.getDamage((Player) damagePlayer);
                    double kick = PlayerAddAttributes.getKick((Player) damagePlayer);
                    event.setDamage( damage);
                    ((EntityDamageByEntityEvent) event).setKnockBack((float) kick);
                    PlayerEffects playerEffects = PlayerEffects.getInstance(damagePlayer.getName());
                    for (BaseEffect baseEffect:damageEffects){
                        if(baseEffect instanceof PlayerEffect){
                            Skill skill = RsWeaponSkill.getSkill(baseEffect.getBufferName());
                            if(skill != null){
                                addSkill(damagePlayer,entity,skill,baseEffect, event);
                            }
                        }
                        addMineCraftEffects(entity, (Player) damagePlayer, playerEffects, baseEffect);
                    }
                }
            }

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null){
            if(RsWeapon.addHealth.containsKey(player.getName())){
                defaultAPI.removePlayerAttributeInt(player.getName()
                        , baseAPI.PlayerAttType.HEALTH,RsWeapon.addHealth.get(player.getName()));
                RsWeapon.addHealth.remove(player.getName());
            }
        }
    }

    private void addSkill(Entity damagePlayer,Entity entity,Skill skill,BaseEffect baseEffect,EntityDamageEvent event){
        float damage = event.getDamage();
        PlayerEffects playerEffects = PlayerEffects.getInstance(damagePlayer.getName());
        if(playerEffects.containsEffect(baseEffect)){
            String skillName = skill.getName();
            switch (skillName) {
                case PlayerEffect.ICE:
                    if (entity instanceof Player) {
                        PlayerEffects playerEffects2 = PlayerEffects.getDamageEffect(entity.getName());
                        if (baseEffect.getBufferName().equals(PlayerEffect.ICE)) {
                            playerEffects2.addEffect(((PlayerEffect) baseEffect).clone());
                            playerEffects.addEffect(((PlayerEffect) baseEffect).clone());
                            ((Player) damagePlayer).sendTip(TextFormat.AQUA + "冰冻触发 持续" + baseEffect.getTime() + "冷却 " + baseEffect.getCold() + " 秒");
                        }
                    }
                    break;
                case PlayerEffect.MaxDamage:
                    playerEffects.addEffect(((PlayerEffect) baseEffect).clone());
                    int c = baseEffect.getTime();
                    if (c > 0) {
                        float r = c / 100;
                        event.setDamage(damage + (damage * r));
                    }
                    Effects.addMaxDamage(entity);
                    ((Player) damagePlayer).sendTip(TextFormat.RED + "会心一击 触发 冷却 " + baseEffect.getCold() + " 秒");
                    break;
                case PlayerEffect.FRAME:
                    playerEffects.addEffect(((PlayerEffect) baseEffect).clone());
                    entity.setOnFire(baseEffect.getTime());
                    ((Player) damagePlayer).sendTip(TextFormat.RED + "引燃触发 持续" + baseEffect.getTime() + "冷却 " + baseEffect.getCold() + " 秒");
                    break;
                case PlayerEffect.ADD_HEALTH:
                    playerEffects.addEffect(((PlayerEffect) baseEffect).clone());
                    float d = event.getDamage() * baseEffect.getTime() / 100;
                    damagePlayer.setHealth(damagePlayer.getHealth() + d);
                    Effects.addHealth(damagePlayer);
                    ((Player) damagePlayer).sendTip(TextFormat.RED + "吸血触发 血量 +" + d + " 冷却 " + baseEffect.getCold() + " 秒");
                    break;
                case PlayerEffect.LIGHTNING:
                    playerEffects.addEffect(((PlayerEffect) baseEffect).clone());
                    Effects.toLight(baseEffect.getTime(), entity);
                    ((Player) damagePlayer).sendTip(TextFormat.RED + "雷击触发 伤害:" + baseEffect.getTime() + "冷却 " + baseEffect.getCold() + " 秒");
                    break;
                default:break;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntity(EntityDamageEvent event){
        if(event instanceof EntityDamageByEntityEvent){
            Entity entity = event.getEntity();
            Entity damagePlayer = ((EntityDamageByEntityEvent) event).getDamager();
            if(damagePlayer == null || entity == null){
                return;
            }
            if(entity instanceof Player){
                PlayerEffects playerEffects2 = PlayerEffects.getInstance(entity.getName());
                LinkedList<BaseEffect> entityEffects = PlayerAddAttributes.getEffects((Player) entity);
                int dDamage = PlayerAddAttributes.getArmor((Player) entity);
                double dKick = PlayerAddAttributes.getDKick((Player) entity);
                float damage = event.getFinalDamage();
                float kick = ((EntityDamageByEntityEvent) event).getKnockBack();
                damage = damage - dDamage;
                kick = kick - (float) dKick;
                int toDamage = PlayerAddAttributes.getToDamage((Player) entity);
                for (BaseEffect effect:entityEffects){

                    if(effect instanceof PlayerEffect){
                        Skill skill = RsWeaponSkill.getSkill(effect.getBufferName());
                        if(skill != null){
                            if(effect.getBufferName().equals(PlayerEffect.SHIELD)){
                                if(playerEffects2.containsEffect(effect)){
                                    playerEffects2.addEffect(((PlayerEffect) effect).clone());
                                    damage -= damage * effect.getTime() / 100;
                                    ((Player) entity).sendTip("...防护盾效果触发...");
                                    Effects.addRelief(entity);
                                }
                            }
                            addSkill(entity,damagePlayer,skill,effect,event);
                        }
                    }
                    addMineCraftEffects(entity, (Player) damagePlayer, playerEffects2, effect);
                }
                if(!event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)){
                    if(damage > 0){
                        if(toDamage > 0){
                            float toD = damage * toDamage / 100;
                            damagePlayer.attack(new EntityDamageEvent(entity,EntityDamageEvent.DamageCause.SUFFOCATION,toD));
                        }
                    }
                }
                if(kick < 0){
                    kick = 0F;
                }
                if(damage < 0){
                    damage = 0F;
                }
                event.setDamage(damage);
                ((EntityDamageByEntityEvent) event).setKnockBack(kick);
            }
        }
    }

    private void addMineCraftEffects(Entity entity, Player damagePlayer, PlayerEffects playerEffects2, BaseEffect effect) {
        if(effect instanceof MineCraftEffect){
            if (playerEffects2.containsEffect(effect)) {
                playerEffects2.addEffect(((MineCraftEffect) effect).clone());
                if (!entity.hasEffect(((MineCraftEffect) effect).getEffect().getId())) {
                    entity.addEffect(((MineCraftEffect) effect).getEffect());
                    damagePlayer.sendTip(TextFormat.RED + effect.getBufferName()+ "触发 持续:" + effect.getTime() + "冷却 " + effect.getCold() + " 秒");
                }
            }
        }
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player death = event.getEntity();
        EntityDamageEvent damageEvent = death.getLastDamageCause();
        if(damageEvent instanceof EntityDamageByEntityEvent){
            Entity damager = ((EntityDamageByEntityEvent) damageEvent).getDamager();
            if(damager instanceof Player){
                Item item = ((Player) damager).getInventory().getItemInHand();
                if(Weapon.isWeapon(item)){
                    Weapon weapon = Weapon.getInstance(item);
                    if(weapon != null){
                        event.setDeathMessage("");
                        Server.getInstance().broadcastMessage(weapon.getDeathMessage()
                                .replace("{damager}",damager.getName())
                                .replace("{name}",weapon.getName())
                                .replace("{player}",death.getName()));
                    }
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemInHand(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        Item item = event.getItem();
        if(Weapon.isWeapon(item)){
            Weapon weapon = Weapon.getInstance(item);
            if(weapon != null){
                if(!weapon.canUseWeapon(player)){
                    player.sendMessage("§c此武器无法使用...");
                }
            }
        }
        if(Armor.isArmor(item)){
            Armor armor = Armor.getInstance(item);
            if(armor != null){
                if(!armor.canUseArmor(player)){
                    player.sendMessage("§c此盔甲无法使用...");
                }
            }

        }
    }


    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(RsWeapon.damages.containsKey(player.getName())){
            PlayerEffects effects = PlayerEffects.getDamageEffect(player.getName());
            BaseEffect effect = effects.getEffect(PlayerEffect.ICE);
            if(effect != null){
                event.setCancelled();
                player.level.addParticle(new DestroyBlockParticle(new Vector3(player.x,player.y+2,player.z), Block.get(79,0)));
                player.sendTip(TextFormat.AQUA+"你被冰冻了 "+effect.getTime()+" 秒内不能移动");
            }
        }

    }


}
