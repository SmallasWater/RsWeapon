package weapon.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import me.onebone.economyapi.EconomyAPI;
import weapon.RsWeapon;
import weapon.items.BaseItem;

public class MasterCommand extends Command {
    public MasterCommand(String name) {
        super(name,"武器装备认主指令","/ms help",new String[]{"绑定","认主"});
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            if(strings.length > 0){
                if("绑定".equals(strings[0])) {
                    if (!RsWeapon.getInstance().master.containsKey(commandSender)) {
                        Item hand = ((Player) commandSender).getInventory().getItemInHand();
                        BaseItem item = BaseItem.getBaseItem(hand);
                        if (item != null) {
                            if (item.canSetMaster()) {
                                if (item.hasMaster()) {
                                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                                    commandSender.sendMessage("§r§a[认主系统]§c此" + (item.isWeapon() ? "武器" : "盔甲") + "已认主: " + item.getMaster());
                                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                                    return true;
                                } else {
                                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                                    commandSender.sendMessage("§r§a[认主系统]§6再输入一次/ms 确认认主 (需花费:" + item.getMoney() + "金币)");
                                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                                    RsWeapon.getInstance().master.put(((Player) commandSender).getPlayer(), item);
                                    return true;
                                }
                            }
                        }
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§a[认主系统]§c请手持武器 或 盔甲");
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    } else {
                        BaseItem item = RsWeapon.getInstance().master.get(commandSender);
                        RsWeapon.getInstance().master.remove(commandSender);
                        Item hand = ((Player) commandSender).getInventory().getItemInHand();
                        BaseItem item1 = BaseItem.getBaseItem(hand);
                        if (item1 != null) {
                            if (item1.equals(item)) {
                                if(EconomyAPI.getInstance().reduceMoney(commandSender.getName(),item.getMoney(),true) == 1){
                                    if(item1.setMaster(commandSender.getName())){
                                        ((Player) commandSender).getInventory().setItemInHand(item1.toItem());
                                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                                        commandSender.sendMessage("§r§a[认主系统]§b"+ (item.isWeapon() ? "武器" : "盔甲") +"认主成功! 拥有者:"+commandSender.getName());
                                        commandSender.sendMessage("§r§a[认主系统]§e花费: "+item.getMoney());
                                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                                    }else{
                                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                                        commandSender.sendMessage("§r§a[认主系统]§c此物品无法认主");
                                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                                    }
                                }else{
                                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                                    commandSender.sendMessage("§r§a[认主系统]§c抱歉，，您的金钱不足"+item.getMoney());
                                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                                }
                                return true;
                            }
                        }
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§a[认主系统]§c请不要认主其他" + (item.isWeapon() ? "武器" : "盔甲"));
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        return true;
                    }
                }
//                if("解绑".equals(strings[0])){
//                    remove((Player) commandSender);
//                    return true;
//                }
                if("help".equals(strings[0])){
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    commandSender.sendMessage("§r§a/绑定 绑定 绑定手中的武器或盔甲");
//                    commandSender.sendMessage("§r§a/绑定 解绑 解绑手中的武器或盔甲");
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                }
            }
        }
        return false;
    }

    public void remove(Player commandSender){
        Item hand = commandSender.getInventory().getItemInHand();
        BaseItem item = BaseItem.getBaseItem(hand);
        if (item != null) {
            if(item.hasMaster()){
                String master = item.getMaster();
                if(master.equals(commandSender.getName())){
                    if(item.setMaster(null)){
                        commandSender.getInventory().setItemInHand(item.toItem());
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§a[认主系统]§b"+ (item.isWeapon() ? "武器" : "盔甲") +"解绑成功!");
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    }else{
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§a[认主系统]§c此物品无法解绑");
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    }
                }
            }else{
                commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                commandSender.sendMessage("§r§a[认主系统]§c此物品未绑定");
                commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            }
        }
    }
}
