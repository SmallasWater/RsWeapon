package weapon.players;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import weapon.RsWeapon;
import weapon.items.Armor;
import weapon.items.BaseItem;
import weapon.items.Weapon;
import weapon.players.effects.BaseEffect;
import weapon.players.effects.MineCraftEffect;
import weapon.players.effects.PlayerEffect;
import weapon.task.PlayerAddHealthTask;
import weapon.utils.Effects;
import weapon.utils.PlayerAddAttributes;


import java.util.LinkedList;

public class OnListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        RsWeapon.playerHealth.put(player.getName(), player.getMaxHealth());
        Server.getInstance().getScheduler().scheduleRepeatingTask(new PlayerAddHealthTask(player),20);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event){
        if(event instanceof EntityDamageByEntityEvent){
            Entity entity = event.getEntity();
            Entity damagePlayer = ((EntityDamageByEntityEvent) event).getDamager();
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
                            if(playerEffects.containsEffect(baseEffect)){
                                if(entity instanceof Player){
                                    PlayerEffects playerEffects2 = PlayerEffects.getDamageEffect(entity.getName());
                                    if(((PlayerEffect) baseEffect).getBufferName().equals(PlayerEffect.ICE)){
                                        playerEffects2.addEffect(((PlayerEffect) baseEffect).clone());
                                        playerEffects.addEffect(((PlayerEffect) baseEffect).clone());
                                        ((Player) damagePlayer).sendTip(TextFormat.AQUA + "冰冻触发 持续" + baseEffect.getTime() + "冷却 " +baseEffect.getCold() + " 秒");

                                    }
                                }
                                if(((PlayerEffect) baseEffect).getBufferName().equals(PlayerEffect.FRAME)){
                                    playerEffects.addEffect(((PlayerEffect) baseEffect).clone());
                                    entity.setOnFire(baseEffect.getTime());
                                    ((Player) damagePlayer).sendTip(TextFormat.RED + "引燃触发 持续" + baseEffect.getTime() + "冷却 " +baseEffect.getCold() + " 秒");
                                }
                                if(((PlayerEffect) baseEffect).getBufferName().equals(PlayerEffect.ADD_HEALTH)){
                                    playerEffects.addEffect(((PlayerEffect) baseEffect).clone());
                                    float d = event.getDamage() *  baseEffect.getTime() / 100;
                                    damagePlayer.setHealth(damagePlayer.getHealth() + d);
                                    Effects.addHealth(damagePlayer);
                                    ((Player) damagePlayer).sendTip(TextFormat.RED + "吸血触发 血量 +"+d+" 冷却 " +baseEffect.getCold() + " 秒");
                                }
                                if(((PlayerEffect) baseEffect).getBufferName().equals(PlayerEffect.LIGHTNING)){
                                    playerEffects.addEffect(((PlayerEffect) baseEffect).clone());
                                    toLight(baseEffect.getTime(),entity);
                                    ((Player) damagePlayer).sendTip(TextFormat.RED + "雷击触发 伤害:" + baseEffect.getTime() + "冷却 " +baseEffect.getCold() + " 秒");
                                }
                            }
                        }
                        if(baseEffect instanceof MineCraftEffect) {
                            if (playerEffects.containsEffect(baseEffect)) {
                                playerEffects.addEffect(((MineCraftEffect) baseEffect).clone());
                                if (!entity.hasEffect(((MineCraftEffect) baseEffect).getEffect().getId())) {
                                    Effect effect = ((MineCraftEffect) baseEffect).getEffect();
                                    entity.addEffect(((MineCraftEffect) baseEffect).getEffect());
                                    ((Player) damagePlayer).sendTip(TextFormat.RED + BaseItem.getEffectStringById(effect.getId())
                                            + BaseItem.getLevelByString(effect.getAmplifier()) + "触发 持续:" + baseEffect.getTime() + "冷却 " + baseEffect.getCold() + " 秒");
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntity(EntityDamageEvent event){
        if(event instanceof EntityDamageByEntityEvent){
            Entity entity = event.getEntity();
            Entity damagePlayer = ((EntityDamageByEntityEvent) event).getDamager();
            if(entity instanceof Player){
                if(event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)){
                    return;
                }
                PlayerEffects playerEffects2 = PlayerEffects.getInstance(entity.getName());
                LinkedList<BaseEffect> entityEffects = PlayerAddAttributes.getEffects((Player) entity);
                int dDamage = PlayerAddAttributes.getArmor((Player) entity);
                double dKick = PlayerAddAttributes.getDKick((Player) entity);
                float damage = event.getDamage();
                float kick = ((EntityDamageByEntityEvent) event).getKnockBack();
                damage = damage - dDamage;
                kick = kick - (float) dKick;
                if(kick < 0){
                    kick = 0F;
                }
                if(damage < 0){
                    damage = 0F;
                }
                int toDamage = PlayerAddAttributes.getToDamage((Player) entity);
                for (BaseEffect effect:entityEffects){
                    if(effect instanceof PlayerEffect){
                        if(((PlayerEffect) effect).getBufferName().equals(PlayerEffect.SHIELD)){
                            if(playerEffects2.containsEffect(effect)){
                                playerEffects2.addEffect(((PlayerEffect) effect).clone());
                                damage -= damage * effect.getTime() / 100;
                                ((Player) entity).sendTip("...防护盾效果触发...");
                                Effects.addRelief(entity);
                            }

                        }
                    }
                }
                if(toDamage > 0){
                    float toD = damage * toDamage / 100;
                    damagePlayer.attack(new EntityDamageEvent(entity,EntityDamageEvent.DamageCause.SUFFOCATION,toD));
                }
                event.setDamage(damage);
                ((EntityDamageByEntityEvent) event).setKnockBack(kick);
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
                    Weapon weapon = Weapon.getWeapon(item);
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
            Weapon weapon = Weapon.getWeapon(item);
            if(weapon != null){
                if(!weapon.canUseWeapon(player)){
                    player.sendMessage("§c此武器无法使用...");
                }
            }
        }
        if(Armor.isArmor(item)){
            Armor armor = Armor.getArmor(item);
            if(!armor.canUseArmor(player)){
                player.sendMessage("§c此盔甲无法使用...");
            }
        }
    }


    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(RsWeapon.effects.containsKey(player.getName())){
            PlayerEffects effects = PlayerEffects.getInstance(player.getName());
            BaseEffect effect = effects.containsEffect();
            if(effect != null){
                event.setCancelled();
                player.level.addParticle(new DestroyBlockParticle(new Vector3(player.x,player.y+2,player.z), Block.get(79,0)));
                player.sendTip(TextFormat.AQUA+"你被冰冻了 "+effect.getTime()+" 秒内不能移动");
            }
        }

    }

    private void toLight(int damage,Entity entity){
        FullChunk chunk = entity.getLevel().getChunk((int)entity.getX() >> 4, (int)entity.getZ() >> 4);
        CompoundTag nbt = new CompoundTag();
        nbt.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("",entity.x))
                .add(new DoubleTag("", entity.y))
                .add(new DoubleTag("", entity.z)));
        nbt.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", 0.0D))
                .add(new DoubleTag("", 0.0D))
                .add(new DoubleTag("", 0.0D))
        );
        nbt.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("", 0.0F))
                .add(new FloatTag("", 0.0F))
        );
        EntityLightning lightning = new EntityLightning(chunk, nbt);
        lightning.attack((float) damage);
        lightning.setEffect(false);
        lightning.spawnToAll();
    }
}
