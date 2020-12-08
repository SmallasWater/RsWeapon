package weapon.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import me.onebone.economyapi.EconomyAPI;
import weapon.RsWeapon;
import weapon.items.BaseItem;

public class ClearCommand extends Command{
    public ClearCommand(String name) {
        super(name,"武器 & 盔甲 洗练","/cw help");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            Item item = ((Player) commandSender).getInventory().getItemInHand();
            BaseItem item1 = BaseItem.getBaseItem(item);
            if(item1 != null){
                if(item1.isWeapon() || item1.isArmor()){
                    if(EconomyAPI.getInstance().myMoney(commandSender.getName()) >= RsWeapon.getInstance().getClearMoney()){
                        if(item1.toRarity()){
                            EconomyAPI.getInstance().reduceMoney(commandSender.getName(),RsWeapon.getInstance().getClearMoney());
                            ((Player) commandSender).getInventory().setItemInHand(item1.toItem());
                            commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                            commandSender.sendMessage("§r§d[洗练系统]§b洗练成功!");
                            commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        }
                        return true;
                    }else{
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§d[洗练系统]§c洗练失败 金钱不足");
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        return true;
                    }
                }
            }
            commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            commandSender.sendMessage("§r§d[洗练系统]§c请手持 武器 或 盔甲");
            commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
        }
        return true;
    }
}
