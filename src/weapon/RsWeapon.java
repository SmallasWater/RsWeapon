package weapon;

import AwakenSystem.data.baseAPI;
import AwakenSystem.data.defaultAPI;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import updata.AutoData;
import weapon.floatingtext.TextEntity;
import weapon.commands.*;
import weapon.items.*;
import weapon.players.OnListener;
import weapon.players.effects.PlayerEffects;
import weapon.task.FixPlayerInventoryTask;
import weapon.task.ForeachPlayersTask;
import weapon.utils.PlayerAddAttributes;
import weapon.utils.RsWeaponSkill;
import weapon.utils.Skill;

import java.io.File;
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
                    if(weapon != null){
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
    public int getUpDataLevel(){
        return getConfig().getInt("强化等级限制",12);
    }

    public int getUpDataAttribute(int r){
        return PlayerAddAttributes.getNumberUp(getConfig().get("强化增加属性",1),r);
    }

    public int getUpDataMoney(int level){
        return getConfig().getInt("强化消耗金币",5000) * (level + 1);
    }

    private boolean getCanShowInventory(){
        return getConfig().getBoolean("装备是否显示创造背包",true);
    }

    @Override
    public void onDisable() {
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


    public Rarity getLevelUpByString(int up){
        if(rarity.size() > up){
            return rarity.get(up);
        }
        return new Rarity("???","0-1");
    }
}
