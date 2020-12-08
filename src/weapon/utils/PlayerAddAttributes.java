package weapon.utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.ServerException;
import weapon.RsWeapon;
import weapon.items.Armor;

import weapon.items.BaseItem;
import weapon.items.Weapon;
import weapon.items.suits.ItemSuit;
import weapon.items.suits.PlayerSuit;
import weapon.items.suits.Suit;
import weapon.players.effects.BaseEffect;
import weapon.players.effects.MineCraftEffect;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Random;

public class PlayerAddAttributes {

    private static LinkedList<BaseItem> getItems(Player player){
        LinkedList<BaseItem> items = new LinkedList<>();
        Item item = player.getInventory().getItemInHand();
        if(Weapon.isWeapon(item)){
            Weapon weapon = Weapon.getInstance(item);
            if(weapon != null){
                if(weapon.canUse(player)){
                    items.add(weapon);
                }
            }
        }
        for (Item armorItem:player.getInventory().getArmorContents()){
            if(Armor.isArmor(armorItem)){
                Armor armor = Armor.getInstance(armorItem);
                if(armor != null){
                    if(armor.canUse(player)){
                        items.add(armor);
                    }
                }
            }
        }
        return items;
    }

    public static int getHealth(Player player){
        LinkedList<BaseItem> items = getItems(player);
        LinkedList<PlayerSuit> suits = getSuits(player);
        int health = 0;
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Armor){
                    health += ((Armor) item).getHealth();
                }
            }
        }
        if(suits.size() > 0){
            for(PlayerSuit suit:suits){
                health += Integer.parseInt(suit.getMath(Suit.Adds.HEALTH));
            }
        }
        return health;
    }

    public static int getNumberUp(Object r,int up){
        if(r instanceof String){
            String r1 = r.toString();
            if(r1.matches("^([0-9.]+)[ ]*%$")) {
                int a1 = Integer.parseInt(r1.split("%")[0]);
                a1 /= 100;
                if (a1 > 0) {
                    return (up * a1);
                }
            }
        }else if(r instanceof Integer){
            return ((Integer)r);
        }
        return 0;

    }

    public static int getDamage(Player player){
        LinkedList<BaseItem> items = getItems(player);
        LinkedList<PlayerSuit> suits = getSuits(player);
        int damage = 0;
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Weapon){
                    damage = ((Weapon) item).getFinalDamage();
                }
            }
        }
        if(suits.size() > 0){
            for(PlayerSuit suit:suits){
                damage += Integer.parseInt(suit.getMath(Suit.Adds.DAMAGE));
            }
        }

        return damage;
    }

    public static LinkedList<PlayerSuit> getSuits(Player player){
        LinkedList<PlayerSuit> suits = new LinkedList<>();
        LinkedList<String> armorName = new LinkedList<>();
        for(BaseItem item1:getItems(player)){
            armorName.add(item1.getName());
        }
        for(ItemSuit suit: RsWeapon.cacheSuit){
            int i = 0;
            for(String s:suit.getItemNames()){
                if(armorName.contains(s)){
                    i++;
                }
            }
            for(Suit suit1:suit.getSuits()){
                if(i >= suit1.getCount()){
                    suits.add(new PlayerSuit(suit.getName(),suit1.getCount(),suit1.getAll()));
                }
            }
        }
        return suits;
    }

    public static int getArmor(Player player){
        LinkedList<BaseItem> items = getItems(player);
        LinkedList<PlayerSuit> suits = getSuits(player);
        int armor = 0;
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Armor){
                    armor += ((Armor) item).getArmor();
                }
            }
        }
        if(suits.size() > 0){
            for(PlayerSuit suit:suits){
                armor += Integer.parseInt(suit.getMath(Suit.Adds.ARMOR));
            }
        }
        return armor;
    }

    public static int getToDamage(Player player){
        LinkedList<BaseItem> items = getItems(player);
        LinkedList<PlayerSuit> suits = getSuits(player);
        int armor = 0;
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Armor){
                    armor += ((Armor) item).getToDamage();
                }
            }
        }
        if(suits.size() > 0){
            for(PlayerSuit suit:suits){
                armor += Integer.parseInt(suit.getMath(Suit.Adds.TO_DAMAGE));
            }
        }
        return armor;
    }

    public static double getDKick(Player player){
        LinkedList<BaseItem> items = getItems(player);
        LinkedList<PlayerSuit> suits = getSuits(player);
        double armor = 0;
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Armor){
                    armor += ((Armor) item).getDKick();
                }
            }
        }
        if(suits.size() > 0){
            for(PlayerSuit suit:suits){
                armor += Double.parseDouble(suit.getMath(Suit.Adds.DEL_KICK));
            }
        }
        return armor;
    }


    public static double getKick(Player player){
        LinkedList<BaseItem> items = getItems(player);
        LinkedList<PlayerSuit> suits = getSuits(player);
        double kick = 0;
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Weapon){
                    kick = ((Weapon) item).getKick();
                }
            }
        }
        if(suits.size() > 0){
            for(PlayerSuit suit:suits){
                kick += Double.parseDouble(suit.getMath(Suit.Adds.KICK));
            }
        }
        return kick;
    }


    public static LinkedList<BaseEffect> getEffects(Player player){
        LinkedList<BaseEffect> effects = new LinkedList<>();
        LinkedList<BaseItem> items = getItems(player);
        LinkedList<PlayerSuit> suits = getSuits(player);
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Armor){
                    for(BaseEffect effect:((Armor) item).getEffects()){
                        if(!effects.contains(effect)){
                            effects.add(effect);
                        }
                    }
                }
                if(item instanceof Weapon){
                    for(BaseEffect effect:((Weapon) item).getEffects()){
                        if(!effects.contains(effect)){
                            effects.add(effect);
                        }
                    }
                }
            }
        }
        for(PlayerSuit suit:suits){
            String effect = suit.getMath(Suit.Adds.EFFECT);
            String[] s = effect.split(",");
            for(String s1:s){
                if(!"0".equals(s1) && !"".equals(s1)){
                    Effect effect2;
                    try {
                        effect2 = Effect.getEffect(Integer.parseInt(s1.split(":")[0]))
                                .setAmplifier(Integer.parseInt(s1.split(":")[1])).setDuration(100);
                    }catch (ServerException e){
                        continue;
                    }
                    effects.add(new MineCraftEffect(effect2,0));
                }
            }

        }
        return effects;
    }


    public static LinkedList<BaseEffect> getDamages(Player player){
        LinkedList<BaseEffect> effects = new LinkedList<>();
        LinkedList<BaseItem> items = getItems(player);
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Weapon){
                    for (BaseEffect effect:((Weapon) item).getDamages()){
                        if(!effects.contains(effect)){
                            effects.add(effect);
                        }
                    }
                }
            }
        }
        return effects;
    }


    public String getStrReplace(Player player, String string){
        string = string.replace("{we-damage}",getDamage(player)+"")
                .replace("{we-armor}",getArmor(player)+"")
                .replace("{we-health}",getHealth(player)+"")
                .replace("{we-kick}",String.format("%.2f",getDKick(player)))
                .replace("{we-dkick}",String.format("%.2f",getDKick(player)))
                .replace("{we-todamage}",getToDamage(player)+"");
        return string;
    }


    public static Suit.Adds getSuitByString(String name){
        if("伤害".equalsIgnoreCase(name) || "damage".equalsIgnoreCase(name)){
            return Suit.Adds.DAMAGE;
        }
        if("防御".equalsIgnoreCase(name) || "armor".equalsIgnoreCase(name)){
            return Suit.Adds.ARMOR;
        }
        if("血量".equalsIgnoreCase(name) || "health".equalsIgnoreCase(name)){
            return Suit.Adds.HEALTH;
        }
        if("抗击退".equalsIgnoreCase(name) || "dkick".equalsIgnoreCase(name)){
            return Suit.Adds.DEL_KICK;
        }
        if("反伤".equalsIgnoreCase(name) || "todamage".equalsIgnoreCase(name)){
            return Suit.Adds.TO_DAMAGE;
        }
        if("击退".equalsIgnoreCase(name) || "kick".equalsIgnoreCase(name)){
            return Suit.Adds.KICK;
        }
        if("药水".equalsIgnoreCase(name) || "effect".equalsIgnoreCase(name)){
            return Suit.Adds.EFFECT;
        }
        return null;
    }
}
