package weapon.items;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Config;
import me.onebone.economyapi.EconomyAPI;
import weapon.RsWeapon;
import weapon.players.effects.BaseEffect;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class Weapon extends BaseItem {

    private int level;

    private String name;

    private int min;

    private int max;

    private String type;

    private double kick;

    private int count;

    private String deathMessage;

    private String message;

    private boolean unBreak;



    private static String tagName = "RsWeapon_Weapon";

    private LinkedList<GemStone> gemStoneLinkedList = new LinkedList<>();

    private Weapon(Item item){
        this.item = item;
        this.init();
    }

    private Weapon(Item item, int min, int max, double kick, int level,int count,boolean unBreak,String deathMessage){
        this.item = item;
        this.min = min;
        this.max = max;
        this.kick = kick;
        this.level = level;
        this.count = count;
        this.unBreak = unBreak;
        this.deathMessage = deathMessage;
    }


    public static Weapon getInstance(String name) {
        if(Weapon.inArray(name)){
            if(RsWeapon.CaCheWeapon.containsKey(name)){
                RsWeapon.CaCheWeapon.get(name);
            }
            return toWeapon(name);
        }
        return null;
    }

    public static Weapon getWeapon(Item item){
        if(Weapon.isWeapon(item)){
            return new Weapon(item);
        }
        return null;
    }

    public static String getWeaponName(Item item){
        if(Weapon.isWeapon(item)){
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

    /**
     * 武器被动技能
     * @return 被动
     * */
    public LinkedList<BaseEffect> getEffects(){
        LinkedList<BaseEffect> effects = new LinkedList<>();
        if(gemStoneLinkedList.size() > 0){
            for (GemStone stone:gemStoneLinkedList) {
                effects.addAll(stone.getWeaponEffect());
            }
        }
        return effects;
    }


    /**
     * 武器主动技能
     * @return 主动
     * */
    public LinkedList<BaseEffect> getDamages(){
        LinkedList<BaseEffect> effects = new LinkedList<>();
        if(gemStoneLinkedList.size() > 0){
            for (GemStone stone:gemStoneLinkedList) {
                effects.addAll(stone.getWeaponDamages());
            }
        }
        return effects;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private static Weapon toWeapon(String name){
        Config config = new Config(RsWeapon.getInstance().getWeaponFile()+"/"+name+".yml");
        String id = config.getString("武器外形");
        Item item = BaseItem.toItemByMap(id);
        String type = config.getString("类型");
        Map damages = (Map) config.get("武器攻击力");
        int min = 0,max = 0;
        if(damages.containsKey("min")){
            min = (int) damages.get("min");
        }
        if(damages.containsKey("max")){
            max = (int) damages.get("max");
        }
        double kick = config.getDouble("武器击退");
        int level = config.getInt("武器品阶");
        Map enchant = (Map) config.get("武器附魔");
        item.addEnchantment(BaseItem.getEnchant(enchant));

        int count = config.getInt("镶嵌数量");

        boolean un = config.getBoolean("无限耐久");

        String deathMessage = config.getString("击杀提示");
        Weapon weapon = new Weapon(item,min,max,kick,level,count,un,deathMessage);
        weapon.setMessage(config.getString("介绍"));
        weapon.setType(type);
        weapon.setName(name);
        return weapon;
    }



    private void setName(String name) {
        this.name = name;
    }

    private String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    public static Weapon getInstance(Item item){
        if(Weapon.isWeapon(item)){
            return new Weapon(item);
        }
        return null;
    }

    @Override
    public CompoundTag getCompoundTag(){
        CompoundTag tag = item.getNamedTag();
        return super.getCompoundTag(tag,unBreak,name,tagName,gemStoneLinkedList);
    }

    @Override
    public Item toItem() {
        Item item = Item.get(this.item.getId(),this.item.getDamage());
        CompoundTag tag = getCompoundTag();
        reload(tag);
        item = getItemName(item,tag,this.name,tagName);
        item.setLore(lore());
        return item;
    }

    public static void generateWeapon(String name,String id){
        if(!Weapon.inArray(name)){
            RsWeapon.getInstance().saveResource("weapon.yml","/Weapon/"+name+".yml",false);
            Config config = new Config(RsWeapon.getInstance().getWeaponFile()+"/"+name+".yml",Config.YAML);
            config.set("武器外形",id);
            config.save();
            RsWeapon.CaCheWeapon.put(name,Weapon.getInstance(name));
        }
    }


    private String[] lore(){//13
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r§6◈类型   §6◈§a武器");
        lore.add("§r§6◈耐久   §6◈"+(unBreak?"§7无限耐久":(item.getMaxDurability() != -1?"§c会损坏":"§a无耐久")));
        lore.add("§r§6◈品阶   §6◈"+RsWeapon.levels.get(level).getName());
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r"+message.trim());
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r§6◈§7攻击§6◈ §a"+min+" - "+max);
        lore.add("§r§6◈§7击退§6◈ §a"+kick);
        lore.add("§r§6◈§7宝石§6◈ "+getStoneString(gemStoneLinkedList));
        lore.add("§r§6◈§f═§7╞════════════╡§f═");
        if(gemStoneLinkedList.size() > 0){
            lore.add(skillToString(gemStoneLinkedList,true));
        }
        return lore.toArray(new String[0]);
    }

    public String getName() {
        return this.name;
    }

    /**解析Tag*/
    private void init(){
        CompoundTag tag = item.getNamedTag();
        this.name = tag.getString(tagName+"name");
        ListTag tags = tag.getList(tagName+"Gem");
        gemStoneLinkedList = this.getGemStoneByTag(tags);
        reload(tag);
    }

    private void reload(CompoundTag tag){
        Weapon weapon = Weapon.getInstance(name);
        if(weapon != null){
            this.level = weapon.level;
            this.kick = weapon.kick;
            this.min = weapon.min;
            this.max = weapon.max;
            this.count = weapon.count;
            this.type = weapon.type;
            this.deathMessage = weapon.deathMessage;
            this.message = weapon.message;
            this.unBreak = weapon.unBreak;

            for (GemStone stone: gemStoneLinkedList) {
                this.kick += stone.getKick();
                this.min += stone.getMin();
                this.max += stone.getMax();
            }
            if(tag.contains(tagName+"upData")){
                for(int level = 1;level <= tag.getInt(tagName+"upData");level++){
                    int add =  RsWeapon.getInstance().getUpDataAttribute();
                    if(add > 0){
                        this.kick += (double) (add / 10);
                        this.min += add;
                        this.max += add;
                    }
                }
            }
        }
    }

    public int getCount() {
        return count;
    }

    public double getKick() {
        return kick;
    }

    public int getLevel() {
        return level;
    }

    public int getDamage() {
        return new Random().nextInt(max)+min;
    }

    public static boolean isWeapon(Item item){
        if(item.hasCompoundTag()){
            CompoundTag tag = item.getNamedTag();
            if(tag.contains(BaseItem.TAG_NAME)){
                return tag.getString(BaseItem.TAG_NAME).equals(tagName);
            }
        }
        return false;
    }



    public static boolean inArray(String name) {
        File gem = new File(RsWeapon.getInstance().getWeaponFile()+"/"+name+".yml");
        return gem.exists();
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public boolean isUnBreak() {
        return unBreak;
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

    public boolean upData(Player player){
        int money = (int) EconomyAPI.getInstance().myMoney(player);
        if(money < RsWeapon.getInstance().getUpDataMoney()){
            player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            player.sendMessage("§r§c抱歉 您的金钱不足 无法强化");
            player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
        }else{
            if(canUpData()){
                player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                player.sendMessage("§r§c抱歉 此武器无法强化");
                player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            }else{
                EconomyAPI.getInstance().reduceMoney(player,RsWeapon.getInstance().getUpDataMoney(),true);
                toUpData(tagName);
                player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                player.sendMessage("§r§e恭喜 武器强化成功");
                player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canUpData() {
        CompoundTag tag = this.item.getNamedTag();
        if(tag.contains(tagName+"upData")){
            return tag.getInt(tagName + "upData") == RsWeapon.getInstance().getUpDataLevel();
        }
        return false;
    }

}
