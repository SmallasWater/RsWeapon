package weapon.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import weapon.items.Armor;
import weapon.items.GemStone;
import weapon.items.Weapon;

public class ShowMessageCommand extends Command {
    public ShowMessageCommand(String name) {
        super(name,"显示手持装备信息","手持武器输/wm");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            Item item = ((Player)commandSender).getInventory().getItemInHand();
            String type = null;
            String[] showList = null;
            if(GemStone.isGemStone(item)){
                GemStone wm = GemStone.getInstance(item);
                if(wm != null){
                    type = "宝石";
                    showList = wm.lore();
                }
            }else if(Armor.isArmor(item)){
                Armor armor = Armor.getInstance(item);
                if(armor != null){
                    type = "盔甲";
                    showList = armor.lore();
                }
            }else if(Weapon.isWeapon(item)){
                Weapon weapon = Weapon.getInstance(item);
                if(weapon != null){
                    type = "武器";
                    showList = weapon.lore();
                }
            }
            if(showList != null){
                FormWindowSimple simple = new FormWindowSimple(type+"显示","");
                StringBuilder b = new StringBuilder();
                for(String str:showList){
                    b.append(str).append("\n");
                }
                simple.setContent(b.toString());
                ((Player) commandSender).showFormWindow(simple);
            }else{
                commandSender.sendMessage("不是RsWeapon 系列物品 无法显示");
            }

        }

        return true;
    }
}
