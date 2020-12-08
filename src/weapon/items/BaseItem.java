package weapon.items;

import AwakenSystem.AwakenSystem;
import AwakenSystem.data.baseAPI;
import AwakenSystem.data.defaultAPI;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import weapon.RsWeapon;
import weapon.players.effects.BaseEffect;
import weapon.players.effects.MineCraftEffect;
import weapon.players.effects.PlayerEffect;
import weapon.utils.RsWeaponSkill;
import weapon.utils.Skill;

import java.util.*;

/** @author 若水*/
public abstract class BaseItem implements Cloneable{

    String type;

    boolean canUp;

    int levelUp = 0;

    int count,money;
    final static String TAG_NAME = "RsWeaponName";
    Item item;
    String master;

    String name;

    String message;

    public String getName() {
        return name;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }


    public boolean upData(Player player){
        return false;
    }

    public int getMoney() {
        return money;
    }

    public String getMaster() {
        return master;
    }

    public boolean canSetMaster(){
        return false;
    }

    public boolean setMaster(String name){
        return false;
    }

    public boolean hasMaster(){
        return master != null;
    }

    public void setLevelUp(int levelUp) {
        this.levelUp = levelUp;
    }


    public int getLevelUp() {
        return levelUp;
    }

    static Item toItemByMap(String id){
        return Item.fromString(id);
    }

    static ArrayList<Enchantment> getEnchant(List<Map> ench){
        ArrayList<Enchantment> m = new ArrayList<>();
        for (Map enchant:ench){
            int eid = 0,l = 0;
            if(enchant.containsKey("id")){
                eid = (int) enchant.get("id");
            }
            if(enchant.containsKey("level")){
                l = (int) enchant.get("level");
            }
            if(l > 0){
                m.add(Enchantment.getEnchantment(eid).setLevel(l));
            }
        }
        return m;
    }

    public void setCanUp(boolean canUp) {
        this.canUp = canUp;
    }

    public boolean isCanUp() {
        return canUp;
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
                        GemStone stone = GemStone.getInstance(((StringTag) o).data);
                        if(stone != null) {
                            stones.add(GemStone.getInstance(((StringTag) o).data));
                        }
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
        String[] array = new String[]{"I","II","III","IV","V","VI","VII","VIII","IX","X","XI","XII","XIII"};
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
                Skill skill = RsWeaponSkill.getSkill(effect.getBufferName());
                if(skill != null){
                    builder.append("§r")
                            .append(type)
                            .append("§r")
                            .append(effect.getBufferName()).append("\n");
                    builder.append(skill.getMessage().replace("%i%",effect.getTime()+"")
                            .replace("%cold%",effect.getCold()+""))
                            .append("\n");
                }
            }
            if(effect instanceof MineCraftEffect){
                builder.append("§r")
                        .append(type)
                        .append("§r")
                        .append(effect.getBufferName()).append("\n")
                        .append("§7持续: ")
                        .append(effect.getTime()).append(" 秒 ")
                        .append("§7冷却: ")
                        .append(effect.getCold()).append(" 秒")
                        .append("\n");
            }
        }
        return builder;

    }



    static String getItemName(Item item){
        if(item.hasCompoundTag()){
            CompoundTag tag = item.getNamedTag();
            if(tag.contains(BaseItem.TAG_NAME)){
                return tag.getString(BaseItem.TAG_NAME);

            }
        }
        return null;
    }

    String skillToString(LinkedList<GemStone> gemStoneLinkedList,boolean isWeapon){
        StringBuilder builder = new StringBuilder();
        LinkedList<BaseEffect> effects = new LinkedList<>();
        LinkedList<BaseEffect> damages = new LinkedList<>();
        for (GemStone stone:gemStoneLinkedList) {
            if(isWeapon){
                if(stone.getWeaponEffect().size() > 0){
                    for(BaseEffect effect:stone.getWeaponEffect()){
                        if(!effects.contains(effect)){
                            effects.add(effect);
                        }
                    }
                }
                if(stone.getWeaponDamages().size() > 0){
                    for(BaseEffect effect:stone.getWeaponDamages()){
                        if(!damages.contains(effect)){
                            damages.add(effect);
                        }
                    }
                }
            }else{
                if(stone.getArmorEffect().size() > 0){
                    for(BaseEffect effect:stone.getArmorEffect()){
                        if(!effects.contains(effect)){
                            effects.add(effect);
                        }
                    }
                }
                if(stone.getArmorDamages().size() > 0){
                    for(BaseEffect effect:stone.getArmorDamages()){
                        if(!damages.contains(effect)){
                            damages.add(effect);
                        }
                    }
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

    public boolean removeStone(GemStone stone){
        return false;
    }

    public boolean canRemove(GemStone stone){
        return false;

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

    private boolean exit(LinkedList<String> list, String type){
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

    private ArrayList<String> getRPGList(int rpgLevel,String rpgAttribute,int rpgPF){
        ArrayList<String> lore = new ArrayList<>();
        if(Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null){
            lore.add("§r§f═§7╞════════════╡§f═");
            lore.add("§r§e◎§b限制等级§e◎  §f"+rpgLevel);
            lore.add("§r§e◎§b限制评级§e◎  §f"+getChatMessageAll(rpgPF));
            lore.add("§r§e◎§b限制属性§e◎  §f"+("".equals(rpgAttribute)?"无":rpgAttribute));
        }
        return lore;
    }

    ArrayList<String> getListByRPG(int rpgLevel,String rpgAttribute,int rpgPF,String message){
        ArrayList<String> lore = new ArrayList<>();
        ArrayList<String> listTag = getRPGList(rpgLevel,rpgAttribute,rpgPF);
        if(listTag.size() > 0){
            lore.addAll(listTag);
        }
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r"+message.trim());
        lore.add("§r§f═§7╞════════════╡§f═");
        return lore;
    }

    boolean canUse(Player player, int rpgLevel, String rpgAttribute, int rpgPF){
        if(Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null){
            int playerLevel = defaultAPI.getPlayerAttributeInt(player.getName(), baseAPI.PlayerConfigType.LEVEL);
            String playerAttribute = defaultAPI.getPlayerAttributeString(player.getName(), baseAPI.PlayerConfigType.ATTRIBUTE);
            if(playerAttribute == null || "null".equals(playerAttribute)){
                playerAttribute = "";
            }
            int playerPF = defaultAPI.getPlayerAttributeInt(player.getName(), baseAPI.PlayerConfigType.TALENT);
            if(playerLevel >= rpgLevel){
                if(!"".equals(rpgAttribute)){
                    String[] s = rpgAttribute.split(",");
                    if(Arrays.asList(s).contains(playerAttribute)){
                        return playerPF >= rpgPF;
                    }
                }else{
                    return playerPF >= rpgPF;
                }
            }
            return false;
        }
        return true;
    }

    boolean runCanInlay(GemStone stone, LinkedList<GemStone> gemStoneLinkedList
            , int level, int count, String rpgAttribute, int rpgPF){
        if(stone != null){
            if(exit(stone.getxItem(),getType())){
                if(gemStoneLinkedList.contains(stone)){
                    return false;
                }else{
                    if(level >= stone.getxLevel()){
                        if(count >= (gemStoneLinkedList.size()+1)){
                            String ban = stone.getxAttribute();
                            if(!"".equals(ban)) {
                                boolean isTrue = false;
                                for (String s : ban.split(",")) {
                                    if(new LinkedList<>(Arrays.asList(rpgAttribute.split(","))).contains(s)){
                                        isTrue = true;
                                        break;
                                    }
                                }
                                if(isTrue){
                                    return stone.getXpf() <= rpgPF;
                                }
                            }else{
                                return stone.getXpf() <= rpgPF;
                            }

                        }else{
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean canInlay(GemStone stone){

        return false;
    }

    /**
     * 显示lore信息
     * @return lore数组
     */
    abstract public String[] lore();

    public boolean inlayStone(GemStone stone){
        return false;
    }


    public boolean canUse(Player player){
        return true;
    }

    public boolean toRarity(){
        return false;
    }




    public static BaseItem getBaseItem(Item item){
        if(Weapon.isWeapon(item)){
            return Weapon.getInstance(item);
        }
        if(Armor.isArmor(item)){
            return Armor.getInstance(item);
        }
        if(GemStone.isGemStone(item)){
            return GemStone.getInstance(item);
        }
        return null;
    }

    public boolean isArmor(){
        return false;
    }

    public boolean isWeapon(){
        return false;
    }

    public boolean isGemStone(){
        return false;
    }

    private static String getChatMessageAll(int pf) {
        try{
            HashMap map = (HashMap) AwakenSystem.getMain().getConfig().get(baseAPI.ConfigType.SETTING.getName());
            int count = 0;
            LinkedHashMap<Integer, String> m = new LinkedHashMap<>();
            for(Iterator var3 = map.keySet().iterator(); var3.hasNext(); ++count) {
                Object string = var3.next();
                m.put(count, String.valueOf(string));
            }
            return m.get(pf);
        }catch (Exception e){
            return "?";
        }

    }

}
