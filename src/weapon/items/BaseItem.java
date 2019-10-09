package weapon.items;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import weapon.players.effects.BaseEffect;
import weapon.players.effects.MineCraftEffect;
import weapon.players.effects.PlayerEffect;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** @author 若水*/
public abstract class BaseItem implements Cloneable{

    int count;
    final static String TAG_NAME = "RsWeaponName";
    Item item;



    static Item toItemByMap(String id){
        return Item.fromString(id);
    }

    static Enchantment getEnchant(Map enchant){
        int eid = 0,l = 0;
        if(enchant.containsKey("id")){
            eid = (int) enchant.get("id");
        }
        if(enchant.containsKey("level")){
            l = (int) enchant.get("level");
        }
        return Enchantment.getEnchantment(eid).setLevel(l);
    }

    public CompoundTag getCompoundTag(){
        return new CompoundTag();
    }

    CompoundTag getCompoundTag(CompoundTag tag,boolean unBreak,String name, String tagName, LinkedList<GemStone> gemStoneLinkedList){
        tag.putString(TAG_NAME,tagName);
        tag.putString(tagName+"name",name);
        if(unBreak){
            tag.putByte("Unbreakable",1);
        }
        ListTag<StringTag> tagListTag = new ListTag<>(tagName+"Gem");
        for(GemStone stone:gemStoneLinkedList){
            tagListTag.add(new StringTag(stone.getName(),stone.getName()));
        }
        tag.putList(tagListTag);
        return tag;
    }
    LinkedList<GemStone> getGemStoneByTag(ListTag tags){
        LinkedList<GemStone> stones = new LinkedList<>();
        if(tags != null){
            List list = tags.getAll();
            if(list != null){
                for(Object o:list){
                    if(o instanceof StringTag){
                        stones.add(GemStone.getInstance(((StringTag) o).data));
                    }
                }
            }
        }
        return stones;
    }

    /**
     * 此方法返回转换的物品
     * @return 物品
     * */
    abstract public Item toItem();

    public static String getEffectStringById(int id){
        String[] array = new String[]{"","§f急速","§c减速","§e破坏","§4疲劳","§c强壮","§a瞬间回血",
                "§4真实伤害","§b跳跃增幅","§4眩晕","§a治疗","§7护甲","§6炎热抗性",
                "§9深海行走","§7隐身","§4致盲","§8夜间视力","§4饥饿者","§4虚弱","§4毒素","§4死亡诅咒",
                "§d血量增益","§e伤害吸收","§6饱食者","§d飞行","剧毒?",""};
        try{
            return array[id];
        }catch (ArrayIndexOutOfBoundsException e) {
            return array[0];
        }
    }

    public static String getLevelByString(int level){
        String[] array = new String[]{"","I","II","III","IV","V","VI","VII","VIII","IX","X","XI","XII"};
        if(level <= 12){
            return array[level];
        }else{
            return "";
        }
    }



    StringBuilder skillGetString(LinkedList<BaseEffect> effects, String type){
        StringBuilder builder = new StringBuilder();
        if(effects.size() == 0){
            builder.append("§c无");
            return builder;
        }
        for(BaseEffect effect:effects){
            if(effect instanceof PlayerEffect){
                builder.append("§r")
                        .append(type)
                        .append("§r")
                        .append(((PlayerEffect) effect).getBufferName()).append("\n");
                if(PlayerEffect.SHIELD.equals(((PlayerEffect) effect).getBufferName())){
                    builder.append("§7抵抗伤害: ");
                    builder.append(effect.getTime()).append(" % ");
                }else if(PlayerEffect.ADD_HEALTH.equals(((PlayerEffect) effect).getBufferName())) {
                    builder.append("§7吸收伤害: ");
                    builder.append(effect.getTime()).append(" % ");
                }else if(PlayerEffect.LIGHTNING.equals(((PlayerEffect) effect).getBufferName())){
                    builder.append("§7伤害: ");
                    builder.append(effect.getTime());
                }else{
                    builder.append("§7持续: ");
                    builder.append(effect.getTime()).append(" 秒 ");
                }
                builder.append("§7冷却: ")
                        .append(effect.getCold()).append(" 秒")
                        .append("\n");
            }
            if(effect instanceof MineCraftEffect){
                builder.append("§r")
                        .append(type)
                        .append("§r")
                        .append(BaseItem.getEffectStringById(((MineCraftEffect) effect).getEffect().getId()))
                        .append(BaseItem.getLevelByString(((MineCraftEffect) effect).getEffect().getAmplifier())).append("\n")
                        .append("§7持续: ")
                        .append(effect.getTime()).append(" 秒 ")
                        .append("§7冷却: ")
                        .append(effect.getCold()).append(" 秒")
                        .append("\n");
            }
        }
        return builder;

    }

    String skillToString(LinkedList<GemStone> gemStoneLinkedList,boolean isWeapon){
        StringBuilder builder = new StringBuilder();
        LinkedList<BaseEffect> effects = new LinkedList<>();
        LinkedList<BaseEffect> damages = new LinkedList<>();
        for (GemStone stone:gemStoneLinkedList) {
            if(isWeapon){
                if(stone.getWeaponEffect().size() > 0){
                    effects.addAll(stone.getWeaponEffect());
                }
                if(stone.getWeaponDamages().size() > 0){
                    damages.addAll(stone.getWeaponDamages());
                }
            }else{
                if(stone.getArmorEffect().size() > 0){
                    effects.addAll(stone.getArmorEffect());
                }
                if(stone.getArmorDamages().size() > 0){
                    damages.addAll(stone.getArmorDamages());
                }
            }
        }
        if(effects.size() > 0){
            builder.append(skillGetString(effects,"§b[被动]"));
        }
        if(damages.size() > 0){
            builder.append(skillGetString(damages,"§9[主动]"));
        }
        return builder.toString();
    }

    StringBuilder getStoneString(LinkedList<GemStone> gemStoneLinkedList){
        StringBuilder builder = new StringBuilder();
        if(gemStoneLinkedList.size() > 0){
            int i = 0;
            for(GemStone stone:gemStoneLinkedList){
                if(i > 1){
                    builder.append("\n");
                    i = 0;
                }
                builder.append(stone.getName()).append(" ");
                i++;
            }
        }else{
            builder.append("§c  无");
        }
        return builder;
    }

    Item getItemName(Item item,CompoundTag tag,String name,String tagName){
        item.setCompoundTag(tag);
        item.setCustomName(name);
        if(tag.contains(tagName+"upData")){
            item.setCustomName(name+"  §c+"+tag.getInt(tagName+"upData"));
        }
        return item;
    }

    boolean exit(LinkedList<String> list,String type){
        for (String s:list){
            if(s.equals(type)){
                return true;
            }
        }
        return false;
    }


    public boolean canUpData(){
        return false;
    }

    void toUpData(String tagName){
        CompoundTag tag = item.getNamedTag();
        if(!tag.contains(tagName+"upData")){
            tag.putInt(tagName+"upData",0);
        }
        tag.putInt(tagName+"upData",tag.getInt(tagName+"upData") + 1);
        item.setCompoundTag(tag);
    }



}
