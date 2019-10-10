package weapon;

import AwakenSystem.data.baseAPI;
import AwakenSystem.data.defaultAPI;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import weapon.commands.ClickCommand;
import weapon.commands.UpDataCommand;
import weapon.commands.WeCommand;
import weapon.items.Armor;
import weapon.items.GemStone;
import weapon.items.ItemLevel;
import weapon.items.Weapon;
import weapon.players.OnListener;
import weapon.players.PlayerEffects;
import weapon.task.FixPlayerInventoryTask;
import weapon.task.ForeachPlayersTask;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;


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

    public static LinkedHashMap<String,Integer> playerHealth = new LinkedHashMap<>();

    public static LinkedHashMap<String,Integer> addHealth = new LinkedHashMap<>();

    public static LinkedHashMap<String , GemStone> GemStones = new LinkedHashMap<>();

    public static LinkedHashMap<String, Weapon> CaCheWeapon = new LinkedHashMap<>();

    public static LinkedHashMap<String, Armor> CaCheArmor = new LinkedHashMap<>();



    @Override
    public void onEnable() {
        instance = this;
        File gemFiles = getGemFile();
        File weaponFile = getWeaponFile();
        File armorFile = getArmorFile();
        this.saveDefaultConfig();
        this.reloadConfig();
        levels = initLevel();

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
        this.getLogger().info("开始加载配置文件");
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
        this.getServer().getCommandMap().register("",new WeCommand("we"));
        this.getServer().getCommandMap().register("",new UpDataCommand("ups"));
        long t2 = System.currentTimeMillis();
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

    private LinkedList<ItemLevel> initLevel(){
        LinkedList<ItemLevel> levels = new LinkedList<>();
        Config config = this.getConfig();
        List<String> map =  config.getStringList("武器等级");
        for(String o:map){
            levels.add(new ItemLevel(o));

        }
        return levels;
    }


    private void loadGemStone(){
        File file = new File(this.getDataFolder()+"/GemStone");
        File[] files = file.listFiles();
        if(files != null){
            for(File file1:files){
                if(file1.isFile()){
                    String names = file1.getName().substring(0,file1.getName().lastIndexOf("."));
                    GemStones.put(names,GemStone.getInstance(names));
                    this.getLogger().info(names+" 宝石加载成功");
                }
            }
        }
    }

    private void loadWeapon(){
        File file = new File(this.getDataFolder()+"/Weapon");
        File[] files = file.listFiles();
        if(files != null){
            for(File file1:files){
                if(file1.isFile()){
                    String names = file1.getName().substring(0,file1.getName().lastIndexOf("."));
                    CaCheWeapon.put(names,Weapon.getInstance(names));
                    this.getLogger().info(names+" 武器加载成功");
                }
            }
        }
    }

    private void loadArmor(){
        File file = new File(this.getDataFolder()+"/Armor");
        File[] files = file.listFiles();
        if(files != null){
            for(File file1:files){
                if(file1.isFile()){
                    String names = file1.getName().substring(0,file1.getName().lastIndexOf("."));
                    CaCheArmor.put(names,Armor.getInstance(names));
                    this.getLogger().info(names+" 盔甲加载成功");
                }
            }
        }
    }

    public int getUpDataLevel(){
        return getConfig().getInt("强化等级限制",12);
    }

    public int getUpDataAttribute(){
        return getConfig().getInt("强化增加属性",1);
    }

    public int getUpDataMoney(){
        return getConfig().getInt("强化消耗金币",5000);
    }

    @Override
    public void onDisable() {
        for (String playerName: addHealth.keySet()) {
            if (Server.getInstance().getPluginManager().getPlugin("LevelAwakenSystem") != null) {
                defaultAPI.removePlayerAttributeInt(playerName, baseAPI.PlayerAttType.HEALTH,RsWeapon.addHealth.get(playerName));
                RsWeapon.addHealth.remove(playerName);
            }
        }
    }
}
