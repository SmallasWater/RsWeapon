package weapon.utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import weapon.items.Armor;

import weapon.items.BaseItem;
import weapon.items.Weapon;
import weapon.players.effects.BaseEffect;

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
        int health = 0;
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Armor){
                    health += ((Armor) item).getHealth();
                }
            }
        }
        return health;
    }

    public static int getDamage(Player player){
        LinkedList<BaseItem> items = getItems(player);
        int damage = 0;
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Weapon){
                    damage = new Random().nextInt(((Weapon) item).getMax()) + ((Weapon) item).getMin();
                }
            }
        }
        return damage;
    }

    public static int getArmor(Player player){
        LinkedList<BaseItem> items = getItems(player);
        int armor = 0;
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Armor){
                    armor += ((Armor) item).getArmor();
                }
            }
        }
        return armor;
    }

    public static int getToDamage(Player player){
        LinkedList<BaseItem> items = getItems(player);
        int armor = 0;
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Armor){
                    armor += ((Armor) item).getToDamage();
                }
            }
        }
        return armor;
    }

    public static double getDKick(Player player){
        LinkedList<BaseItem> items = getItems(player);
        double armor = 0;
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Armor){
                    armor += ((Armor) item).getDKick();
                }
            }
        }
        return armor;
    }

    public static double getKick(Player player){
        LinkedList<BaseItem> items = getItems(player);
        double kick = 0;
        if(items.size() > 0){
            for (BaseItem item:items){
                if(item instanceof Weapon){
                    kick = ((Weapon) item).getKick();
                }
            }
        }
        return kick;
    }


    public static LinkedList<BaseEffect> getEffects(Player player){
        LinkedList<BaseEffect> effects = new LinkedList<>();
        LinkedList<BaseItem> items = getItems(player);
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
}
