package weapon.items;


import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Config;
import me.onebone.economyapi.EconomyAPI;
import weapon.RsWeapon;
import weapon.players.effects.BaseEffect;
import java.io.File;
import java.util.*;

public class Weapon extends BaseItem {

    private int level;

    private int min;

    private int max;

    private int rpgLevel;

    private String rpgAttribute;

    private int rpgPF;

    private int money;

    private int updata;

    private String nameTag;

    private String type;

    private double kick;

    private int count;

    private String deathMessage;

    private boolean unBreak;

    private boolean canShow = false;



    private static String tagName = "RsWeapon_Weapon";

    private LinkedList<GemStone> gemStoneLinkedList = new LinkedList<>();

    private Weapon(Item item){
        this.item = item;
        this.init();
    }

    @Override
    public boolean setMaster(String master) {
        this.master = master;
        return true;
    }

    @Override
    public boolean canSetMaster() {
        return true;
    }

    @Override
    public String getMaster() {
        return master;
    }

    private Weapon(Item item, int min, int max, double kick, int level, int count, boolean unBreak, String deathMessage){
        this.item = item;
        this.min = min;
        this.max = max;
        this.kick = kick;
        this.level = level;
        this.count = count;
        this.unBreak = unBreak;
        this.deathMessage = deathMessage;
    }

    @Override
    public boolean isWeapon() {
        return true;
    }

    public static Weapon getInstance(String name) {
        if(Weapon.inArray(name)){
            if(!RsWeapon.CaCheWeapon.containsKey(name)){
                RsWeapon.CaCheWeapon.put(name,toWeapon(name));
            }
            return RsWeapon.CaCheWeapon.get(name);
        }
        return null;
    }


    public String getNameTag() {
        return nameTag;
    }

    public void setNameTag(String nameTag) {
        this.nameTag = nameTag;
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
    
    public int getFinalDamage(){
        return new Random().nextInt((this.getMax() - this.getMin() + 1))+ (this.getMin());
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

    private void setUpdata(int updata) {
        this.updata = updata;
    }

    public int getUpdata() {
        return updata;
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

    private void setMessage(String message) {
        this.message = message;
    }

    public static Weapon toWeapon(String name){
        Config config = new Config(RsWeapon.getInstance().getWeaponFile()+"/"+name+".yml");
        String id = config.getString("武器外形");
        Item item = BaseItem.toItemByMap(id);
        String type = config.getString("类型");
        Map damages = (Map) config.get("武器攻击力");
        int min = 0,max = 0;
        if(damages.containsKey("min")){
            min = (int) damages.get("min");
        }
        int money = config.getInt("绑定花费",100);
        if(damages.containsKey("max")){
            max = (int) damages.get("max");
        }
        double kick = config.getDouble("武器击退");
        int level = config.getInt("武器品阶");
        Object enchantObject = config.get("武器附魔");
        if(enchantObject instanceof Map){
            int enchantId = (int) ((Map)enchantObject).get("id");
            int enchantLevel = (int) ((Map)enchantObject).get("level");
            if(enchantLevel > 0){
                Enchantment aura = Enchantment.getEnchantment(enchantId).setLevel(enchantLevel);
                item.addEnchantment(aura);
            }
        }else if(enchantObject instanceof List){
            List<Map> enchant = config.getMapList("武器附魔");
            ArrayList<Enchantment> enchants = BaseItem.getEnchant(enchant);
            for (Enchantment aura : enchants){
                item.addEnchantment(aura);
            }
        }
        int count = config.getInt("镶嵌数量");

        int updata = config.getInt("强化等级限制",12);
        boolean un = config.getBoolean("无限耐久");
        int rpgLevel = config.getInt("限制等级(rpg)",0);
        int rpgPF = config.getInt("限制评级",0);
        String rpgAttribute = config.getString("限制职业(属性)","");

        String deathMessage = config.getString("击杀提示");
        Weapon weapon = new Weapon(item,min,max,kick,level,count,un,deathMessage);
        Object up = config.get("稀有度");
        int levelUp = 0;
        if(up != null){
            if(up instanceof String){
                if("x".equals(up.toString().toLowerCase())){
                    levelUp = new Random().nextInt(RsWeapon.rarity.size());
                    weapon.setCanUp(true);
                }
            }else if(up instanceof Integer){
                levelUp = (int) up;
            }

        }
        weapon.setLevelUp(levelUp);
        weapon.setRpgAttribute(rpgAttribute);
        weapon.setRpgPF(rpgPF);
        weapon.setRpgLevel(rpgLevel);
        weapon.setMessage(config.getString("介绍"));
        weapon.setType(type);
        weapon.setName(name);
        weapon.setMoney(money);
        weapon.setUpdata(updata);
        weapon.setLevelUp(config.getInt("稀有度"));
        weapon.setNameTag(config.getString("名称",name));

        weapon.setCanShow(config.getBoolean("是否在创造背包显示",false));
        return weapon;
    }

    private int[] getRarity(int r,int max){
        Rarity rarity = RsWeapon.getInstance().getLevelUpByString(r);
        return new int[]{r
                , rarity.getRound((max))};
    }
    public void setCanShow(boolean canShow) {
        this.canShow = canShow;
    }


    public boolean isCanShow() {
        return canShow;
    }

    @Override
    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public int getMoney() {
        return money;
    }

    private void setRpgPF(int rpgPF) {
        this.rpgPF = rpgPF;
    }

    private void setRpgLevel(int rpgLevel) {
        this.rpgLevel = rpgLevel;
    }


    private void setRpgAttribute(String rpgAttribute) {
        this.rpgAttribute = rpgAttribute;
    }

    private void setName(String name) {
        this.name = name;
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
    public boolean equals(Object obj) {
        if(obj instanceof Weapon){
            return ((Weapon) obj).getName().equals(name) && ((Weapon) obj).type.equals(type);
        }
        return false;
    }

    @Override
    public CompoundTag getCompoundTag(){
        CompoundTag tag = item.getNamedTag();
        if(tag == null){
            tag = new CompoundTag();
        }
        tag = super.getCompoundTag(tag,unBreak,name,tagName,gemStoneLinkedList);
        if(master != null){
            tag.putString(tagName+"master",master);
        }else if(tag.contains(tagName+"master")){
            tag.remove(tagName+"master");
        }
        return tag;
    }

    @Override
    public Item toItem() {
        Item item = Item.get(this.item.getId(),this.item.getDamage());
        CompoundTag tag = getCompoundTag();
        reload(tag);
        item = getItemName(item,tag,this.nameTag,tagName);
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


    @Override
    public String[] lore(){//13
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§r§2稀有度:  §r"+RsWeapon.getInstance().getLevelUpByString(levelUp).getName());
        lore.add("§r§f═§7╞════════════╡§f═");
        if(master != null){
            lore.add("§r§6◈§a§l主人: §e"+master);
        }else{
            lore.add("§r§6◈§c§l未绑定");
        }
        lore.add("§r§6◈类型   §6◈§a"+type);
        lore.add("§r§6◈耐久   §6◈"+(unBreak?"§7无限耐久":(item.getMaxDurability() != -1?"§c会损坏":"§a无耐久")));
        lore.add("§r§6◈品阶   §6◈"+RsWeapon.levels.get(level).getName());

        lore.addAll(getListByRPG(rpgLevel,rpgAttribute,rpgPF,message.trim())) ;
        lore.add("§r§6◈§7攻击§6◈ §a"+min+" §e(+"+(getMin() - min)+")"+"§a - "+max+" §e(+"+(getMax() - max)+")");
        lore.add("§r§6◈§7击退§6◈ §a"+String.format("%.1f",getKick())+"");
        lore.add("§r§6◈§7宝石§6◈§7(§a"+(getCount() - getGemStones().size())+"§7)§6◈ "+getStoneString(gemStoneLinkedList));
        lore.add("§r§6◈§f═§7╞════════════╡§f═");
        if(gemStoneLinkedList.size() > 0){
            lore.add(skillToString(gemStoneLinkedList,true));
        }
        return lore.toArray(new String[0]);
    }

    @Override
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



    private int[] getRarity(){
        Weapon weapon = Weapon.getInstance(name);
        if(weapon != null) {
            int r = new Random().nextInt(RsWeapon.rarity.size());
            Rarity rarity = RsWeapon.getInstance().getLevelUpByString(r);
            return new int[]{r
                    , rarity.getRound((weapon.max))};
        }
        return new int[0];
    }
    @Override
    public boolean toRarity() {
        CompoundTag tag = item.getNamedTag();
        tag.putIntArray(Weapon.tagName+"levelUp",getRarity());
        item.setNamedTag(tag);
        return true;
    }

    private void reload(CompoundTag tag){
        Weapon weapon = Weapon.getInstance(name);
        if(weapon != null){
            this.money = weapon.money;
            this.level = weapon.level;
            this.kick = weapon.kick;
            this.min = weapon.min;
            this.max = weapon.max;
            this.count = weapon.count;
            this.type = weapon.type;
            this.deathMessage = weapon.deathMessage;
            this.message = weapon.message;
            this.canShow = weapon.canShow;
            this.unBreak = weapon.unBreak;
            this.rpgAttribute = weapon.rpgAttribute;
            this.rpgLevel = weapon.rpgLevel;
            this.updata = weapon.updata;
            this.rpgPF = weapon.rpgPF;
            this.nameTag = weapon.nameTag;
            if(gemStoneLinkedList.size() > 0){
                for (GemStone stone: gemStoneLinkedList) {
                    if(stone != null) {
                        this.kick += stone.getKick();
                        this.min += stone.getMin();
                        this.max += stone.getMax();
                    }
                }
            }
            if(tag.contains(tagName+"master")){
                this.master = tag.getString(tagName+"master");
            }
            if(tag.contains(tagName+"levelUp")){
                int[] tag1 = item.getNamedTag().getIntArray(tagName+"levelUp");
                this.levelUp = tag1[0];
            }
            if(tag.contains(tagName+"upData")){
                for(int level = 1;level <= tag.getInt(tagName+"upData");level++){
                    int add1 =  RsWeapon.getInstance().getUpDataAttribute(min);
                    int add2 =  RsWeapon.getInstance().getUpDataAttribute(max);
                    if(add1 > 0 && add2 > 0){
                        this.min += add1;
                        this.max += add2;
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

//    public int getDamage() {
//        int min = this.min,max = this.max;
//        int[] tag1 = item.getNamedTag().getIntArray(tagName+"levelUp");
//        int aup = tag1[1];
//        min += aup;
//        max += aup;
//        return new Random().nextInt((max - min + 1))+ (min);
//    }

    public static boolean isWeapon(Item item){
        if(item == null){
            return false;
        }
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
        if(item != null) {
            if(item.hasCompoundTag()) {
                int[] tag1 = item.getNamedTag().getIntArray(tagName + "levelUp");
                if (tag1.length > 0) {
                    return max + tag1[1];
                }
            }
        }
        return max;

    }

    public int getMin() {
        if(item != null) {
            if(item.hasCompoundTag()) {
                int[] tag1 = item.getNamedTag().getIntArray(tagName + "levelUp");
                if (tag1.length > 0) {
                    return min + tag1[1];
                }
            }
        }
        return min;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public boolean isUnBreak() {
        return unBreak;
    }


    @Override
    public boolean inlayStone(GemStone stone){
        if(canInlay(stone)){
            gemStoneLinkedList.add(stone);
            return true;
        }
        return false;
    }


    @Override
    public boolean removeStone(GemStone stone){
        if(canRemove(stone)){
            gemStoneLinkedList.remove(stone);
            return true;
        }
        return false;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public boolean canInlay(GemStone stone){
        return runCanInlay(stone,gemStoneLinkedList,level,count,rpgAttribute,rpgPF);
    }

    @Override
    public boolean canRemove(GemStone stone){
        return gemStoneLinkedList.contains(stone);
    }

    @Override
    public boolean upData(Player player){
        int money = (int) EconomyAPI.getInstance().myMoney(player);
        if(money < RsWeapon.getInstance().getUpDataMoney(levelUp + 1,item.getNamedTag().getInt(tagName + "upData"))){
            player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            player.sendMessage("§r§c抱歉 您的金钱不足 无法强化");
            player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
        }else{
            if(canUpData()){
                player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                player.sendMessage("§r§c抱歉 此武器无法强化");
                player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            }else{
                int r = RsWeapon.getInstance().getMathRound(levelUp + 1,item.getNamedTag().getInt(tagName + "upData"));

                EconomyAPI.getInstance().reduceMoney(player,RsWeapon.getInstance().getUpDataMoney(levelUp+1,item.getNamedTag().getInt(tagName + "upData")),true);
                if(new Random().nextInt(100) <= r){
                    toUpData(tagName);
                    player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    player.sendMessage("§r§e恭喜 武器强化成功");
                    player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    return true;
                }else{
                    player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    player.sendMessage("§r§c武器强化失败..当前成功率: "+r+"％");
                    player.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                }

            }
        }
        return false;
    }

    @Override
    public boolean canUse(Player player){
        return canUse(player,rpgLevel,rpgAttribute,rpgPF) && (master == null || player.getName().equals(master));
    }

    @Override
    public boolean canUpData() {
        CompoundTag tag = this.item.getNamedTag();
        if(tag.contains(tagName+"upData")){
            return tag.getInt(tagName + "upData") == getUpdata();
        }
        return false;
    }

}
