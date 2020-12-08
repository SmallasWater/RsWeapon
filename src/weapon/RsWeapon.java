package weapon;

import AwakenSystem.data.baseAPI;
import AwakenSystem.data.defaultAPI;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import updata.AutoData;
import weapon.floatingtext.TextEntity;
import weapon.commands.*;
import weapon.items.*;
import weapon.items.suits.ItemSuit;
import weapon.items.suits.Suit;
import weapon.players.OnListener;
import weapon.players.effects.PlayerEffects;
import weapon.task.FixPlayerInventoryTask;
import weapon.task.ForeachPlayersTask;
import weapon.utils.PlayerAddAttributes;
import weapon.utils.RsWeaponSkill;
import weapon.utils.Skill;
import weapon.utils.math.Calculator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * @author 若水
 * QQ 1798640336
 * 未经作者允许 严禁倒卖
 * 本插件遵循 LGPL 3.0  协议
 * @version 1.0.0
 * @date 2019/9/7
 *  */
public class RsWeapon extends PluginBase {


    private static RsWeapon instance;
    /** 玩家状态存储*/
    public static LinkedHashMap<String, PlayerEffects> effects = new LinkedHashMap<>();

    /** 玩家被攻击状态存储*/
    public static LinkedHashMap<String, PlayerEffects> damages = new LinkedHashMap<>();

    public static LinkedList<ItemLevel> levels = new LinkedList<>();

    public static LinkedList<Rarity> rarity = new LinkedList<>();

    public LinkedHashMap<Player, BaseItem> master = new LinkedHashMap<>();

    public static LinkedHashMap<String,Integer> playerHealth = new LinkedHashMap<>();

    public static LinkedHashMap<String,Integer> addHealth = new LinkedHashMap<>();

    public static LinkedHashMap<String , GemStone> GemStones = new LinkedHashMap<>();

    public static LinkedHashMap<String, Weapon> CaCheWeapon = new LinkedHashMap<>();

    public static LinkedHashMap<String, Armor> CaCheArmor = new LinkedHashMap<>();

    public static LinkedList<ItemSuit> cacheSuit = new LinkedList<>();

    public static LinkedHashMap<Player,Boolean> show = new LinkedHashMap<>();

    private String mathUpdata = "100 - ({强化等级} * {稀有度})";

    private LinkedHashMap<String,Skin> skins = new LinkedHashMap<>();

    private LinkedHashMap<String,Skin> armorSkin = new LinkedHashMap<>();

    private Config modeConfig;

    private LinkedHashMap<Player,Skin> playerDefaultSkin = new LinkedHashMap<>();


    @Override
    public void onEnable() {
        instance = this;
        if(Server.getInstance().getPluginManager().getPlugin("AutoUpData") != null){
            if(AutoData.defaultUpData(this,getFile(),"SmallasWater","RsWeapon")){
                return;
            }
        }
        File gemFiles = getGemFile();
        File weaponFile = getWeaponFile();
        File armorFile = getArmorFile();
        this.saveDefaultConfig();
        this.reloadConfig();

        levels = initLevel();
        loadRarity();
        if(!gemFiles.exists()){
            if(!gemFiles.mkdirs()){
                Server.getInstance().getLogger().info("/GemStone文件夹创建失败");
            }
        }
        if(!weaponFile.exists()){
            if(!weaponFile.mkdirs()){
                Server.getInstance().getLogger().info("/Weapon文件夹创建失败");
            }
        }
        if(!armorFile.exists()){
            if(!armorFile.mkdirs()){
                Server.getInstance().getLogger().info("/Armor文件夹创建失败");
            }
        }
        if(!getSkillFile().exists()){
            saveResource("skill.yml");
        }
        this.getLogger().info("开始加载配置文件");
        this.getLogger().info("开始读取技能列表");
        loadSkill();
        this.getLogger().info("技能读取完毕");
        long t1 = System.currentTimeMillis();
        this.getLogger().info("开始加载宝石..");
        loadGemStone();

        this.getLogger().info("宝石加载完成..");
        this.getLogger().info("开始加载武器..");
        loadWeapon();
        this.getLogger().info("武器加载完成..");
        this.getLogger().info("开始加载盔甲..");
        loadArmor();
        this.getLogger().info("盔甲加载完成..");
        this.getLogger().info("开始加载套装效果..");
        loadSuit();
        this.getLogger().info("套装效果加载完成..");
//        this.getLogger().info("开始加载盔甲模型..");
//        load4DSkin();
//        this.getLogger().info("盔甲模型加载完成..");
        this.getServer().getPluginManager().registerEvents(new OnListener(),this);
        this.getServer().getScheduler().scheduleRepeatingTask(new ForeachPlayersTask(),20);
        this.getServer().getScheduler().scheduleRepeatingTask(new FixPlayerInventoryTask(),20);
        this.getServer().getCommandMap().register("",new ClickCommand("click"));
        this.getServer().getCommandMap().register("",new MasterCommand("ms"));
        this.getServer().getCommandMap().register("",new WeCommand("we"));
        this.getServer().getCommandMap().register("",new ReloadCommand("up"));
        this.getServer().getCommandMap().register("",new UpDataCommand("ups"));
        this.getServer().getCommandMap().register("",new ShowMessageCommand("wm"));
        this.getServer().getCommandMap().register("",new ClearCommand("cw"));
        long t2 = System.currentTimeMillis();
        Entity.registerEntity("TextEntity", TextEntity.class);
        this.getLogger().info("配置加载完成 用时:"+((t2 - t1) % (1000 * 60))+"ms");
        this.getLogger().info("武器系统加载成功..作者: 若水");
    }

    public LinkedHashMap<Player, Skin> getPlayerDefaultSkin() {
        return playerDefaultSkin;
    }

    public LinkedHashMap<String, Skin> getArmorSkin() {
        return armorSkin;
    }

    public static RsWeapon getInstance() {
        return instance;
    }

    public File getGemFile(){
        return new File(this.getDataFolder()+"/GemStone");
    }

    public File getWeaponFile(){
        return new File(this.getDataFolder()+"/Weapon");
    }

    public File getArmorFile(){
        return new File(this.getDataFolder()+"/Armor");
    }

    private File getSkillFile(){
        return new File(this.getDataFolder()+"/skill.yml");
    }

    private File getSuitFile(){
        return new File(this.getDataFolder()+"/suit.yml");
    }

    private File getModFile(){
        return new File(this.getDataFolder()+"/mod.yml");
    }


    public LinkedList<ItemLevel> initLevel(){
        LinkedList<ItemLevel> levels = new LinkedList<>();
        Config config = this.getConfig();
        List<String> map =  config.getStringList("武器等级");
        for(String o:map){
            levels.add(new ItemLevel(o));

        }
        return levels;
    }

    public void loadGemStone(){
        File file = new File(this.getDataFolder()+"/GemStone");
        File[] files = file.listFiles();
        if(files != null){
            for(File file1:files){
                if(file1.isFile()){
                    String names = file1.getName().substring(0,file1.getName().lastIndexOf("."));
                    GemStone stone = GemStone.toGemStone(names);
                    if(stone != null){
                        GemStones.put(names,stone);
                        if(getCanShowInventory()){
                            if (stone.isCanShow()) {
                                Item.addCreativeItem(stone.toItem());
                            }
                        }
                    }
                }
            }
        }
    }

    public void loadSuit(){
        if(!getSuitFile().exists()){
            saveResource("suit.yml");
        }
        Config config = new Config(getSuitFile(),Config.YAML);
        LinkedHashMap<String,Object> map = config.get("套装效果", new LinkedHashMap<>());
        cacheSuit = new LinkedList<>();
        for(String name:map.keySet()){
            LinkedList<String> itemName = new LinkedList<>();
            //萌新意志
            Map map1 = (Map) map.get(name);
            List items = (List) map1.get("套装");
            for(Object os:items){
                if(CaCheWeapon.containsKey(os.toString()) || CaCheArmor.containsKey(os.toString())){
                    itemName.add(os.toString());
                }
            }
            Map map2 = (Map) map1.get("激活效果");
            //o1为id
            LinkedList<Suit> suits = new LinkedList<>();
            for(Object o1:map2.keySet()){
                LinkedHashMap<Suit.Adds,String> adds = new LinkedHashMap<>();
                int id;
                try{
                    id = Integer.parseInt(o1.toString());
                }catch (Exception e){
                    e.printStackTrace();
                    continue;
                }
                Map map3 = (Map) map2.get(o1);
                Suit.Adds add;
                //o2为加成
                for(Object o2:map3.keySet()){
                    add = PlayerAddAttributes.getSuitByString(o2.toString());
                    if(add != null){
                        String d = "0";
                        try{
                            d = map3.get(o2).toString();
                        }catch (Exception ignore){}
                        adds.put(add,d);
                    }
                }
                suits.add(new Suit(id,adds));
            }
            if(itemName.size() > 0 && suits.size() > 0){
                cacheSuit.add(new ItemSuit(name,itemName,suits));
                this.getLogger().info("成功加载 "+name+"套装效果");
            }else{
                this.getLogger().info("无可用的套装效果");
            }
        }
    }


    private void loadDefaultSkin() {
        if(!new File(this.getDataFolder()+"/Skins").exists()){
            this.getLogger().info("未检测到Skins文件夹，正在创建");
            if(!new File(this.getDataFolder()+"/Skins").mkdirs()){
                this.getLogger().info("Skins文件夹创建失败");
            }
        }
    }
//
//    private void loadSkin() {
//        if(!new File(this.getDataFolder()+"/Skins").exists()){
//            loadDefaultSkin();
//        }
//        File[] files = new File(this.getDataFolder()+"/Skins").listFiles();
//        if(files != null && files.length > 0){
//            for(File file:files){
//                String skinName = file.getName();
//                if(new File(this.getDataFolder()+"/Skins/"+skinName+"/skin.png").exists()){
//                    Skin skin = new Skin();
//                    BufferedImage skindata = null;
//                    try {
//                        skindata = ImageIO.read(new File(this.getDataFolder()+"/Skins/"+skinName+"/skin.png"));
//                    } catch (IOException var19) {
//                        System.out.println("不存在皮肤");
//                    }
//
//                    if (skindata != null) {
//                        skin.setSkinData(skindata);
//                        skin.setSkinId(skinName);
//                    }
//                    //如果是4D皮肤
//                    if(new File(this.getDataFolder()+"/Skins/"+skinName+"/skin.json").exists()){
//                        Map<String, Object> skinJson = (new Config(this.getDataFolder()+"/Skins/"+skinName+"/skin.json", Config.JSON)).getAll();
//                        String geometryName = null;
//                        for (Map.Entry<String, Object> entry1: skinJson.entrySet()){
//                            if(geometryName == null){
//                                geometryName = entry1.getKey();
//                            }
//                        }
//                        skin.setGeometryName(geometryName);
//                        skin.setGeometryData(readFile(new File(this.getDataFolder()+"/Skins/"+skinName+"/skin.json")));
//                    }
//                    this.getLogger().info(skinName+"皮肤读取完成");
//                    skins.put(skinName,skin);
//                }else{
//                    this.getLogger().info("错误的皮肤名称格式 请将皮肤文件命名为 skin.png");
//                }
//            }
//        }
//    }

    private String readFile(File file){
        String content = "";
        try{
            content = Utils.readFile(file);
        }catch (IOException e){
            e.printStackTrace();
        }
        return content;
    }

//    public void load4DSkin() {
//        if (!getModFile().exists()) {
//            saveResource("mod.yml");
//        }
//        loadSkin();
//        Config config = getModeConfig();
//        Map map = (Map) config.get("模型绑定");
//        if (skins.size() > 0) {
//            for (Object armorName : map.keySet()) {
//                String skinName = map.get(armorName).toString();
//                if(skins.containsKey(skinName)){
//                    armorSkin.put(armorName.toString(),skins.get(skinName));
//                    this.getLogger().info("成功加载 "+armorName+"的模型: "+skinName);
//                }
//            }
//        }
//    }

    private Config getModeConfig() {
        if(modeConfig == null){
            modeConfig = new Config(getModFile(),Config.YAML);
        }
        return modeConfig;
    }



    public Skin getPlayerSkinBySkin(Player player, Skin skin){
        Skin skin1 = player.getSkin();
        LinkedHashMap<String,Object> hashMap = decodeSkin(skin1);
        LinkedHashMap<String,Object> hashMap2 = decodeSkin(skin);
        for(String s:hashMap2.keySet()){
            hashMap.put(s,hashMap2.get(s));
        }
        skin1.setGeometryData(toJson(hashMap));
        return skin1;
    }

    private LinkedHashMap<String,Object> decodeSkin(Skin skin){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(skin.getGeometryData(), (new TypeToken<LinkedHashMap<String, Object>>() {
        }).getType());
    }

    private String toJson(LinkedHashMap<String, Object> map){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(map);
    }

    public void loadSkill() {
        if(!getSkillFile().exists()){
            saveResource("skill.yml");
        }
        Config config = new Config(getSkillFile(),Config.YAML);
        Map<String,Object> map = config.getAll();
        for (String skillName:map.keySet()) {
            Map values = (Map) map.get(skillName);
            String message = (String) values.get("效果内容");
            String type = (String) values.get("类型");
            List canUse = (List) values.get("可用装备");
            RsWeaponSkill.addSkill(new Skill(skillName,message,type,(String[])canUse.toArray(new String[0])));
        }
    }

    public void loadWeapon(){
        File file = new File(this.getDataFolder()+"/Weapon");
        File[] files = file.listFiles();
        if(files != null){
            for(File file1:files){
                if(file1.isFile()){
                    String names = file1.getName().substring(0,file1.getName().lastIndexOf("."));
                    Weapon weapon = Weapon.toWeapon(names);
                    CaCheWeapon.put(names,weapon);
                    if(getCanShowInventory()){
                        if (weapon.isCanShow()){
                            Item.addCreativeItem(weapon.toItem());
                        }
                    }

                }
            }
        }
    }

    public void loadArmor(){
        File file = new File(this.getDataFolder()+"/Armor");
        File[] files = file.listFiles();
        if(files != null){
            for(File file1:files){
                if(file1.isFile()){
                    String names = file1.getName().substring(0,file1.getName().lastIndexOf("."));
                    Armor armor = Armor.toArmor(names);
                    CaCheArmor.put(names,armor);
                    if(getCanShowInventory()){
                        if(armor.isCanShow()) {
                            Item.addCreativeItem(armor.toItem());
                        }
                    }

                }
            }
        }
    }


    public int getUpDataAttribute(int r){
        return PlayerAddAttributes.getNumberUp(getConfig().get("强化增加属性",1),r);
    }

    public int getUpDataMoney(int level,int c){
        String s = getConfig().getString("强化消耗金币算法","({稀有度} * 5000) * {强化等级}");
        s = s.replace("{稀有度}",c+"").replace("{强化等级}",level+"").replace(" ","");
        return (int) Calculator.conversion(s);
//        return getConfig().getInt("强化消耗金币",5000) * (level + 1);
    }

    private boolean getCanShowInventory(){
        return getConfig().getBoolean("装备是否显示创造背包",true);
    }

    @Override
    public void onDisable() {
        if (Server.getInstance().getPluginManager().getPlugin("HealthAPI") != null) {
            return;
        }
        for (String playerName: addHealth.keySet()) {
            if (Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null) {
                if(addHealth.containsKey(playerName)){
                    defaultAPI.removePlayerAttributeInt(playerName, baseAPI.PlayerAttType.HEALTH,addHealth.get(playerName));
                    RsWeapon.addHealth.remove(playerName);
                }

            }
        }
    }
    public int getClearMoney(){
        return getConfig().getInt("洗练消耗",10000);
    }

    public void loadRarity(){
        LinkedList<Rarity> rarities = new LinkedList<>();
        List<Map> list = getConfig().getMapList("稀有度");
        if(list.size() == 0){
            list = Rarity.ras;
        }
        for(Map map:list){
            rarities.add(new Rarity(map.get("名称").toString(),map.get("随机范围").toString()));
        }
        rarity = rarities;
    }

    public int getMathRound(int level,int x){
        this.mathUpdata = getConfig().getString("强化成功率算法",mathUpdata);
        String s = this.mathUpdata;
        s = s.replace("{稀有度}",x+"").replace("{强化等级}",level+"").replace(" ","");
        int r = (int) Calculator.conversion(s);
        if(r < 1){
            r = 1;
        }else if(r > 100){
            r = 100;
        }


        return r;
    }

    public Rarity getLevelUpByString(int up){
        if(rarity.size() > up){
            return rarity.get(up);
        }
        return new Rarity("???","0-1");
    }


}
