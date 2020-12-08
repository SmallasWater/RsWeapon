package weapon.commands;



import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import weapon.items.BaseItem;


public class UpDataCommand extends Command {

    public UpDataCommand(String name) {
        super(name,"装备强化","/强化 help",new String[]{"强化"});

    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            Item item = ((Player) commandSender).getInventory().getItemInHand();
            BaseItem item1 = BaseItem.getBaseItem(item);
            if(item1 != null){
                if(item1.isWeapon() || item1.isArmor()){
                    if(item1.upData((Player) commandSender)){
                        ((Player) commandSender).getInventory().setItemInHand(item1.toItem());
                        return true;
                    }else{
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§c强化失败");
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        return true;
                    }
                }
            }
            commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            commandSender.sendMessage("§r§c请手持 武器 或 盔甲");
            commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
        }
        return true;
    }
}
