package weapon.items;


import AwakenSystem.data.baseAPI;
import AwakenSystem.data.defaultAPI;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Config;
import me.onebone.economyapi.EconomyAPI;
import weapon.RsWeapon;
import weapon.players.effects.BaseEffect;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class Armor extends BaseItem{

    private static String tagName = "RsWeapon_Armor";

    private String type;

    private String name;

    private int armor;

    private BlockColor rgb;
    /**
     * 限制等级(rpg): 0
     * 限制职业(属性): ""
     * 限制评级: 0
     * */

    private String message;

    private int rpgLevel;

    private String rpgAttribute;

    private int rpgPF;

    private double dKick;

    private int health;

    private int toDamage;

    private int level;

    private boolean unBreak;


    private LinkedList<GemStone> gemStoneLinkedList = new LinkedList<>();

    private Armor(Item item){
        this.item = item;
        this.init();
    }


    private Armor(Item item, int armor ,double dKick, int health,int toDamage,int level,int count,boolean unBreak){
        this.item = item;
        this.armor = armor;
        this.level = level;
        this.count = count;
        this.unBreak = unBreak;
        this.dKick = dKick;
        this.health = health;
        this.toDamage = toDamage;
    }

    private String getType() {
        return type;
    }



    public BlockColor getRgb() {
        return rgb;
    }


    /**解析Tag*/
    private void init(){
        CompoundTag tag = item.getNamedTag();
        this.name = tag.getString(tagName+"name");
        ListTag tags = tag.getList(tagName+"Gem");
        gemStoneLinkedList = this.getGemStoneByTag(tags);
        reload(tag);
    }

    public static Armor getArmor(Item item){
        return new Armor(item);
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getHealth() {
        return health;
    }

    public int getArmor() {
        return armor;
    }

    public int getCount() {
        return count;
    }

    public double getDKick() {
        return dKick;
    }

    public int getToDamage() {
        return toDamage;
    }

    public boolean isUnBreak() {
        return unBreak;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public static boolean inArray(String name) {
        File gem = new File(RsWeapon.getInstance().getArmorFile()+"/"+name+".yml");
        return gem.exists();
    }

    @Override
    public CompoundTag getCompoundTag() {
        CompoundTag tag = item.getNamedTag();
        return super.getCompoundTag(tag,unBreak,name,tagName,gemStoneLinkedList);
    }

    @Override
    public Item toItem() {
        Item items = Item.get(this.item.getId(),this.item.getDamage());
        CompoundTag tag = getCompoundTag();
        reload(tag);
        items = getItemName(items,tag,this.name,tagName);
        items.setLore(lore());
        items.setCount(1);
        return items;
    }

    private void reload(CompoundTag tag){
        Armor armor = Armor.getInstance(this.name);
        if(armor != null) {
            this.level = armor.level;
            this.dKick = armor.dKick;
            this.armor = armor.armor;
            this.health = armor.health;
            this.toDamage = armor.toDamage;
            this.count = armor.count;
            this.type = armor.type;
            this.message = armor.message;
            this.rgb = armor.rgb;
            this.unBreak = armor.unBreak;
            this.rpgPF = armor.rpgPF;
            this.rpgLevel = armor.rpgLevel;
            this.rpgAttribute = armor.rpgAttribute;
            for (GemStone stone : gemStoneLinkedList) {
                this.armor += stone.getArmor();
                this.health += stone.getHealth();
                this.dKick += stone.getDKick();
                this.toDamage += stone.getToDamage();
            }
            if (tag.contains(tagName + "upData")) {
                for (int level = 0; level < tag.getInt(tagName + "upData"); level++) {
                    int add = RsWeapon.getInstance().getUpDataAttribute();
                    if (add > 0) {
                        this.armor += add;
                        this.health += add;
                        this.dKick += Double.parseDouble(
                                new java.text.DecimalFormat("#.00").format(((float) add / 10)));
                        this.toDamage += add;
                    }
                }
            }
        }
    }



    private String[] lore(){
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r§6◈类型   ◈§e盔甲");
        lore.add("§r§6◈耐久   ◈"+(unBreak?"§7无限耐久":"§c会损坏"));
        lore.add("§r§6◈品阶   ◈"+RsWeapon.levels.get(level).getName());
        lore.addAll(getListByRPG(rpgLevel,rpgAttribute,rpgPF,message.trim()));
        lore.add("§r§6◈§7护甲§6◈    §7+"+armor);
        lore.add("§r§6◈§7韧性§6◈    §7+"+dKick);
        lore.add("§r§6◈§7血量§6◈    §7+"+health);
        lore.add("§r§6◈§7反伤§6◈    §7+"+toDamage);
        lore.add("§r§6◈§7宝石§6◈ "+getStoneString(gemStoneLinkedList));
        lore.add("§r§f═§7╞════════════╡§f═");
        if(gemStoneLinkedList.size() > 0){
            lore.add(skillToString(gemStoneLinkedList,false));
        }
        return lore.toArray(new String[0]);
    }

    public static void generateArmor(String name,String id){
        if(!Armor.inArray(name)){
            RsWeapon.getInstance().saveResource("armor.yml","/Armor/"+name+".yml",false);
            Config config = new Config(RsWeapon.getInstance().getArmorFile()+"/"+name+".yml",Config.YAML);
            config.set("盔甲外形",id);
            config.save();
            RsWeapon.CaCheArmor.put(name,Armor.getInstance(name));
        }
    }

    /**
     * 盔甲没有主动技能
     * @return 被动效果
     * */
    public LinkedList<BaseEffect> getEffects(){
        LinkedList<BaseEffect> effects = new LinkedList<>();
        if(gemStoneLinkedList.size() > 0){
            for (GemStone stone:gemStoneLinkedList) {
                effects.addAll(stone.getArmorEffect());
            }
        }
        return effects;
    }



    private void setRGB(int r, int g, int b){
        rgb = new BlockColor(r,g,b);
    }



    public static boolean isArmor(Item item){
        if(item.hasCompoundTag()){
            CompoundTag tag = item.getNamedTag();
            if(tag.contains(BaseItem.TAG_NAME)){
                return tag.getString(BaseItem.TAG_NAME).equals(tagName);
            }
        }
        return false;
    }

    public static Armor getInstance(String name){
        if(Armor.inArray(name)){
            if(RsWeapon.CaCheArmor.containsKey(name)){
                return RsWeapon.CaCheArmor.get(name);
            }
            return toArmor(name);
        }
        return null;
    }

    public static String getArmorName(Item item){
        if(Armor.isArmor(item)){
            if(item.hasCompoundTag()){
                CompoundTag tag = item.getNamedTag();
                return tag.getString(tagName+"name");
            }
        }
        return null;
    }


    public LinkedList<GemStone> getGemStones() {
        return gemStoneLinkedList;
    }

    private static Armor toArmor(String name){
        Config config = new Config(RsWeapon.getInstance().getArmorFile()+"/"+name+".yml");
        String id = config.getString("盔甲外形");
        String type = config.getString("类型");
        Item item = BaseItem.toItemByMap(id);
        int armor = config.getInt("护甲值");
        int health = config.getInt("增加血量");
        int toDamage = config.getInt("反伤");
        double dKick = config.getDouble("抗击退");
        int level = config.getInt("盔甲品阶");
        Map enchant = (Map) config.get("盔甲附魔");
        item.addEnchantment(BaseItem.getEnchant(enchant));
        Map rgb = (Map) config.get("盔甲染色");
        int r = (int) rgb.get("r");
        int g = (int) rgb.get("g");
        int b = (int) rgb.get("b");
        int count = config.getInt("镶嵌数量");
        String message = config.getString("介绍");
        boolean un = config.getBoolean("无限耐久");
        int rpgLevel = config.getInt("限制等级(rpg)",0);
        int rpgPF = config.getInt("限制评级",0);
        String rpgAttribute = config.getString("限制职业(属性)","");
        Armor armor1 = new Armor(item,armor,dKick,health,toDamage,level,count,un);
        armor1.setRpgLevel(rpgLevel);
        armor1.setRpgPF(rpgPF);
        armor1.setRpgAttribute(rpgAttribute);
        armor1.setRGB(r,g,b);
        armor1.setMessage(message);
        armor1.setType(type);
        armor1.setName(name);
        return armor1;
    }

    public void setRpgAttribute(String rpgAttribute) {
        this.rpgAttribute = rpgAttribute;
    }

    public void setRpgLevel(int rpgLevel) {
        this.rpgLevel = rpgLevel;
    }

    public void setRpgPF(int rpgPF) {
        this.rpgPF = rpgPF;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void inlayStone(GemStone stone){
        if(canInlay(stone)){
            gemStoneLinkedList.add(stone);
        }
    }


    public void removeStone(GemStone stone){
        if(canRemove(stone)){
            gemStoneLinkedList.remove(stone);
        }
    }

    public boolean canInlay(GemStone stone){
        if(exit(stone.getxItem(),getType())){
            if(gemStoneLinkedList.contains(stone)){
                return false;
            }else{
                if(level >= stone.getxLevel()){
                    return count >= (gemStoneLinkedList.size()+1);
                }
            }
        }
        return false;
    }



    public boolean canRemove(GemStone stone){
        return gemStoneLinkedList.contains(stone);

    }

    @Override
    public boolean canUpData() {
        CompoundTag tag = this.item.getNamedTag();
        if(tag.contains(tagName+"upData")){
            return tag.getInt(tagName + "upData") == RsWeapon.getInstance().getUpDataLevel();
        }
        return false;
    }

    public boolean upData(Player player){
        int money = (int) EconomyAPI.getInstance().myMoney(player);
        if(money < RsWeapon.getInstance().getUpDataMoney()){
            player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            player.sendMessage("§r§c抱歉 您的金钱不足 无法强化");
            player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
        }else{
            if(canUpData()){
                player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                player.sendMessage("§r§c抱歉 此盔甲无法强化");
                player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            }else{
                EconomyAPI.getInstance().reduceMoney(player,RsWeapon.getInstance().getUpDataMoney(),true);
                toUpData(tagName);
                player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                player.sendMessage("§r§e恭喜 盔甲强化成功");
                player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                return true;
            }
        }
        return false;
    }

    private void setType(String type) {
        this.type = type;
    }


    public boolean canUseArmor(Player player){
        return canUse(player,rpgLevel,rpgAttribute,rpgPF);
    }
}
