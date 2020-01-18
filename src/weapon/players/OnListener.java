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
import cn.nukkit.item.ItemBow;
import cn.nukkit.item.ItemBowl;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import weapon.RsWeapon;
import weapon.floatingtext.ShowHealthText;
import weapon.items.BaseItem;
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

    private LinkedList<Player> bow = new LinkedList<>();

    @EventHandler
    public void onInt(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Item item = event.getItem();
        if(item != null){
            if(BaseItem.getBaseItem(item) != null){
                if(item instanceof ItemBow || item instanceof ItemBowl){
                    if(event.getAction() == PlayerInteractEvent.Action.PHYSICAL) {
                        bow.add(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        RsWeapon.playerHealth.put(player.getName(), player.getMaxHealth());
        Server.getInstance().getScheduler().scheduleRepeatingTask(new PlayerAddHealthTask(player),20);
        Server.getInstance().getScheduler().scheduleRepeatingTask(new PlayerAddEffectTask(player),20);
    }

  

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event.isCancelled()){
            return;
        }
        if(event instanceof EntityDamageByEntityEvent){
            Entity entity = event.getEntity();
            Entity damagePlayer = ((EntityDamageByEntityEvent) event).getDamager();
            if(damagePlayer == null || entity == null){
                return;
            }
            if(damagePlayer instanceof Player){
                Item item = ((Player) damagePlayer).getInventory().getItemInHand();
                if(Weapon.isWeapon(item)){
                    if(bow.contains(damagePlayer)){
                        bow.remove(damagePlayer);
                        return;
                    }
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
                                addSkill(damagePlayer,entity,skill,baseEffect, (EntityDamageByEntityEvent) event);
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

    private void addSkill(Entity damagePlayer,Entity entity,Skill skill,BaseEffect baseEffect,EntityDamageByEntityEvent event){
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
        if(event.isCancelled()){
            return;
        }
        if(event instanceof EntityDamageByEntityEvent){
            Entity entity = event.getEntity();
            Entity damagePlayer = ((EntityDamageByEntityEvent) event).getDamager();
            if(damagePlayer == null || entity == null){
                return;
            }
            float damage = event.getFinalDamage();
            if(entity instanceof Player){
                PlayerEffects playerEffects2 = PlayerEffects.getInstance(entity.getName());
                LinkedList<BaseEffect> entityEffects = PlayerAddAttributes.getEffects((Player) entity);
                int dDamage = PlayerAddAttributes.getArmor((Player) entity);
                double dKick = PlayerAddAttributes.getDKick((Player) entity);
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
                            addSkill(entity,damagePlayer,skill,effect, (EntityDamageByEntityEvent) event);
                        }
                    }
                    if(damagePlayer instanceof Player){
                        addMineCraftEffects(entity, (Player) damagePlayer, playerEffects2, effect);
                    }
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onShow(EntityDamageEvent event){
        Entity entity = event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();
        if(event.isCancelled()){
            return;
        }
        if(cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
        || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
            if(entity instanceof Player){
                ShowHealthText.send(event.getEntity(),"§l§o§e - §c"+event.getFinalDamage());
            }else{
                ShowHealthText.send(event.getEntity(),"§l§o§e - §a"+event.getFinalDamage());
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
            BaseItem weapon = BaseItem.getBaseItem(item);
            if(weapon != null){
                if(weapon.getMaster() != null){
                    if(!weapon.getMaster().equals(player.getName())){
                        player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        player.sendMessage("§r§e[武器认主]§c 你使用了不属于你的"+weapon.getName());
                        player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        String target = weapon.getMaster();
                        Player t = Server.getInstance().getPlayer(target);
                        if(t != null){
                            t.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                            t.sendMessage("§r§e[武器认主]§d 玩家: "+player.getName()+"手持了属于你的"+weapon.getName());
                            if(t.getInventory().canAddItem(item)){
                                t.sendMessage("§r§e[武器认主]§a 已自动回到你背包");
                                player.getInventory().removeItem(item);
                                t.getInventory().addItem(item);
                            }else{
                                t.sendMessage("§r§e[武器认主]§c 背包满啦，回不去..");
                            }
                            t.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        }
                    }
                    
                }
                if(!weapon.canUse(player)){
                    player.sendMessage("§c此"+(weapon.isWeapon()?"武器":"盔甲")+"无法使用...");
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
