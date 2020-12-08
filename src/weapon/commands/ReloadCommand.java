package weapon.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import weapon.items.Armor;
import weapon.items.GemStone;
import weapon.items.Weapon;

public class ReloadCommand extends Command {

    public ReloadCommand(String name) {
        super(name,"更新装备","/up help",new String[]{"更新"});
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            Item item = ((Player) commandSender).getInventory().getItemInHand();
            if(Weapon.isWeapon(item)){
                Weapon weapon = Weapon.getInstance(item);
                if(weapon != null){
                    ((Player) commandSender).getInventory().setItemInHand(weapon.toItem());
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    commandSender.sendMessage("§r§b武器已重置");
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                }
            }else if(Armor.isArmor(item)){
                Armor armor = Armor.getInstance(item);
                if(armor != null){
                    ((Player) commandSender).getInventory().setItemInHand(armor.toItem());
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    commandSender.sendMessage("§r§b盔甲已重置");
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                }
            }else if(GemStone.isGemStone(item)){
                GemStone gemstone = GemStone.getInstance(item);
                if(gemstone != null){
                    ((Player) commandSender).getInventory().setItemInHand(gemstone.toItem());
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    commandSender.sendMessage("§r§b宝石已重置");
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                }
            }
        }
        return true;
    }
}
