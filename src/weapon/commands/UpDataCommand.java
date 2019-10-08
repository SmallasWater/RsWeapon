package weapon.commands;



import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import weapon.items.Armor;
import weapon.items.Weapon;

public class UpDataCommand extends Command {

    public UpDataCommand(String name) {
        super(name,"装备强化","/强化 help",new String[]{"强化"});

    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            Item item = ((Player) commandSender).getInventory().getItemInHand();
            if(Weapon.isWeapon(item)){
                Weapon weapon = Weapon.getWeapon(item);
                if(weapon != null){
                    if(weapon.upData((Player) commandSender)){
                        ((Player) commandSender).getInventory().setItemInHand(weapon.toItem());
                    }
                }
            }else if(Armor.isArmor(item)){
                Armor armor = Armor.getArmor(item);
                if(armor.upData((Player) commandSender)){
                    ((Player) commandSender).getInventory().setItemInHand(armor.toItem());
                }
            }else{
                commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                commandSender.sendMessage("§r§c请手持 武器 或 盔甲");
                commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            }

        }
        return true;
    }
}
