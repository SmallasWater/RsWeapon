package weapon;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import weapon.commands.ClickCommand;
import weapon.commands.UpDataCommand;
import weapon.commands.WeCommand;
import weapon.items.GemStone;
import weapon.items.ItemLevel;
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

    public static LinkedHashMap<Player,Integer> playerHealth = new LinkedHashMap<>();

    public static LinkedHashMap<Player,Integer> addHealth = new LinkedHashMap<>();

    public static LinkedHashMap<String , GemStone> GemStones = new LinkedHashMap<>();




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

        loadGemStone();

        this.getServer().getPluginManager().registerEvents(new OnListener(),this);
        this.getServer().getScheduler().scheduleRepeatingTask(new ForeachPlayersTask(),20);
        this.getServer().getScheduler().scheduleRepeatingTask(new FixPlayerInventoryTask(),20);
        this.getServer().getCommandMap().register("",new ClickCommand("click"));
        this.getServer().getCommandMap().register("",new WeCommand("we"));
        this.getServer().getCommandMap().register("",new UpDataCommand("ups"));
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

}
