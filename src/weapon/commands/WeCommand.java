package weapon.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemColorArmor;
import cn.nukkit.utils.TextFormat;
import weapon.items.Armor;
import weapon.items.GemStone;
import weapon.items.Weapon;

import java.util.Arrays;
import java.util.List;

public class WeCommand extends Command {
    public WeCommand(String name) {
        super(name,"§c武器系统","/we help");
        this.setPermission("op");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        try {
            if(commandSender.isOp()){
                if(strings.length < 1) {
                    return false;
                }
                List<String> args = Arrays.asList(strings);
                switch (strings[0]){
                    case "help":
                        commandSender.sendMessage("§7=================================");
                        commandSender.sendMessage("§b/we add weapon <武器名称> <ID:Damage> §a添加武器");
                        commandSender.sendMessage("§b/we add armor <盔甲名称> <ID:Damage> §a添加盔甲");
                        commandSender.sendMessage("§b/we give weapon <武器名称> <Player> §a给玩家武器");
                        commandSender.sendMessage("§b/we give armor <盔甲名称> <Player> §a给玩家盔甲");
                        commandSender.sendMessage("§b/we addItem <宝石名称> <ID:Damage> §a添加宝石");
                        commandSender.sendMessage("§b/we giveItem <宝石名称> <Player> §a给玩家宝石");
                        commandSender.sendMessage("§7=================================");
                        break;
                    case "add":
                        String weaponName = args.get(2);
                        String id = args.get(3);
                        switch (args.get(1)){
                            case "weapon":
                                if(Weapon.inArray(weaponName)){
                                    commandSender.sendMessage(TextFormat.RED+"此武器已存在");
                                }else{
                                    Weapon.generateWeapon(weaponName,id);
                                    commandSender.sendMessage(TextFormat.GREEN+"武器创建成功");
                                }
                                break;
                            case "armor":
                                if(Armor.inArray(weaponName)){
                                    commandSender.sendMessage(TextFormat.RED+"此盔甲已存在");
                                }else{
                                    Armor.generateArmor(weaponName,id);
                                    commandSender.sendMessage(TextFormat.GREEN+"盔甲创建成功");
                                }
                                break;
                                default:return false;
                        }
                        break;
                    case "give":
                        String players = args.get(3);
                        String name = args.get(2);
                        Player player = Server.getInstance().getPlayer(players);
                        if(player == null){
                            commandSender.sendMessage(TextFormat.RED+"该玩家不在线");
                            return true;
                        }
                        switch (args.get(1)){
                            case "armor":
                                if(!Armor.inArray(name)){
                                    commandSender.sendMessage(TextFormat.RED+"此盔甲不存在");
                                    return  true;
                                }
                                Armor armor = Armor.getInstance(name);
                                if(armor != null){
                                    Item item = armor.toItem();
                                    if(item instanceof ItemColorArmor){
                                        ((ItemColorArmor) item).setColor(armor.getRgb());
                                    }
                                    player.getInventory().addItem(item);
                                    commandSender.sendMessage(TextFormat.GREEN+"给予成功");
                                }else{
                                    commandSender.sendMessage(TextFormat.RED+"给予失败");
                                }
                                break;
                            case "weapon":
                                if(!Weapon.inArray(name)){
                                    commandSender.sendMessage(TextFormat.RED+"此武器不存在");
                                    return  true;
                                }
                                Weapon weapon = Weapon.getInstance(name);
                                if(weapon != null){
                                    player.getInventory().addItem(weapon.toItem());
                                    commandSender.sendMessage(TextFormat.GREEN+"给予成功");
                                }else{
                                    commandSender.sendMessage(TextFormat.RED+"给予失败");
                                }
                                break;
                                default:return false;
                        }

                        break;
                    case "addItem":
                        if(GemStone.inArray(args.get(1))){
                            commandSender.sendMessage(TextFormat.RED+"此宝石已存在");
                            return  false;
                        }
                        GemStone.generateGemStone(args.get(1),args.get(2));
                        commandSender.sendMessage(TextFormat.GREEN+"宝石创建成功");
                        break;
                    case "giveItem":
                        Player player1 = Server.getInstance().getPlayer(args.get(2));
                        if(player1 == null){
                            commandSender.sendMessage(TextFormat.RED+"该玩家不在线");
                            return false;
                        }
                        String name1 = args.get(1);
                        if(!GemStone.inArray(name1)){
                            commandSender.sendMessage(TextFormat.RED+"此宝石不存在");
                            return  false;
                        }
                        GemStone stone = GemStone.getInstance(name1);
                        if(stone != null){
                            player1.getInventory().addItem(stone.toItem());
                            commandSender.sendMessage(TextFormat.GREEN+"给予成功");
                        }else{
                            commandSender.sendMessage(TextFormat.RED+"给予失败");
                        }
                        break;
                    default:
                        break;
                }

            }
        }catch (ArrayIndexOutOfBoundsException e){
            return false;
        }
        return true;
    }
}
