package weapon.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemColorArmor;
import weapon.items.Armor;
import weapon.items.BaseItem;
import weapon.items.GemStone;
import weapon.items.Weapon;

import java.util.Arrays;
import java.util.List;


public class ClickCommand extends Command {
    public ClickCommand(String name) {
        super(name,"§e镶嵌宝石系统","/click help");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player) {
            if (strings.length < 1 || strings.length > 3){
                commandSender.sendMessage("§c指令错误，请输入/click help 查看");
                return false;
            }
            List<String> args = Arrays.asList(strings);
            switch (args.get(0)){
                case "help":
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    commandSender.sendMessage("/click add <宝石名称> 给手持的§c武器/盔甲§a镶嵌宝石");
                    commandSender.sendMessage("/click remove <宝石名称> 给手持的§c武器/盔甲§a拆除宝石宝石");
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    break;
                case "add":
                    if(args.get(1) == null && "".equals(args.get(1))){
                        commandSender.sendMessage("§c指令错误，请输入/click help 查看");
                        return false;
                    }
                    Item itemHand = ((Player) commandSender).getInventory().getItemInHand();
                    if(Weapon.isWeapon(itemHand)){
                        Weapon weapon = Weapon.getWeapon(itemHand);
                        inviteStone((Player) commandSender,args.get(1),weapon);

                    }else if(Armor.isArmor(itemHand)){
                        Armor armor = Armor.getArmor(itemHand);
                        inviteStone((Player) commandSender,args.get(1),armor);
                    }
                    break;
                case "remove":
                    if(args.get(1) == null && "".equals(args.get(1))){
                        commandSender.sendMessage("§c指令错误，请输入/click help 查看");
                        return false;
                    }
                    Item itemHand1 = ((Player) commandSender).getInventory().getItemInHand();
                    if(Weapon.isWeapon(itemHand1)){
                        Weapon weapon = Weapon.getWeapon(itemHand1);
                        removeStone((Player) commandSender,args.get(1),weapon);

                    }else if(Armor.isArmor(itemHand1)){
                        Armor armor = Armor.getArmor(itemHand1);
                        removeStone((Player) commandSender,args.get(1),armor);
                    }else{
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        commandSender.sendMessage("§r§e[镶嵌系统]§c请手持武器 或 盔甲");
                        commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    }
                    break;
                default:
                    commandSender.sendMessage("§c指令错误，请输入/click help 查看");
                    break;
            }
        }
        return true;
    }

    private void toRemoveStone(GemStone stone,Player player){
        for (Item item:player.getInventory().getContents().values()){
            if(GemStone.isGemStone(item)){
                GemStone stone1 = GemStone.getInstance(item);
                if(stone1 != null){
                    if(stone1.equals(stone)){
                        Item remove = item.clone();
                        if(item.getCount() > 1){
                            remove.setCount(remove.getCount() - 1);
                        }
                        player.getInventory().remove(remove);
                    }
                }
            }
        }
    }


    private boolean canRemoveitem(Player player, String itemName){
        for (Item item:player.getInventory().getContents().values()){
            if(GemStone.isGemStone(item)){
                GemStone stone = GemStone.getInstance(item);
                if(stone != null){
                    if(stone.getName().equals(itemName)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void inviteStone(Player sender, String stoneName, BaseItem item){
        if(canRemoveitem(sender,stoneName)){
            GemStone stone = GemStone.getInstance(stoneName);
            if(item != null){
                if(stone != null){
                    if(item instanceof Weapon){
                        if(((Weapon) item).canInlay(stone)) {
                            ((Weapon) item).inlayStone(stone);
                            sender.getInventory().setItemInHand(item.toItem());
                            toRemoveStone(stone,sender);
                            sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                            sender.sendMessage("§r§e[镶嵌系统]§e恭喜   宝石镶嵌成功  ");
                            sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        }else{
                            sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                            sender.sendMessage("§r§e[镶嵌系统]§c宝石无法镶嵌");
                            sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        }
                    }else if(item instanceof Armor){
                        if(((Armor) item).canInlay(stone)) {
                            ((Armor) item).inlayStone(stone);
                            Item armor = item.toItem();
                            if(armor instanceof ItemColorArmor){
                                ((ItemColorArmor) armor).setColor(((Armor) item).getRgb());
                            }
                            sender.getInventory().setItemInHand(armor);
                            toRemoveStone(stone,sender);
                            sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                            sender.sendMessage("§r§e[镶嵌系统]§e恭喜   宝石镶嵌成功  ");
                            sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        }else{
                            sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                            sender.sendMessage("§r§e[镶嵌系统]§c宝石无法镶嵌");
                            sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        }
                    }else{
                        sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        sender.sendMessage("§r§e[镶嵌系统]§c请手持武器 或 盔甲");
                        sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    }
                }else{
                    sender.sendMessage("§c此宝石无法使用");
                }

            }else {
                sender.sendMessage("§c请手持武器或盔甲！");
            }

        }else{
            sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            sender.sendMessage("§r§e[镶嵌系统]§c抱歉，你背包并没有"+stoneName+"这个宝石 ");
            sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
        }
    }

    private void removeStone(Player sender, String stoneName, BaseItem item){
        GemStone stone = GemStone.getInstance(stoneName);
        if(stone != null){
            if(item instanceof Weapon){
                if(((Weapon) item).canRemove(stone)){
                    ((Weapon) item).removeStone(stone);
                    sender.getInventory().setItemInHand(item.toItem());
                    sender.getInventory().addItem(stone.toItem());
                    sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    sender.sendMessage("§r§e[镶嵌系统]§a宝石拆除成功");
                    sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                }else{
                    sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    sender.sendMessage("§r§e[镶嵌系统]§c抱歉，你武器没有 "+stoneName+"这个宝石");
                    sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                }
            }else if(item instanceof Armor){
                if(((Armor) item).canRemove(stone)){
                    ((Armor) item).removeStone(stone);
                    Item armor = item.toItem();
                    if(armor instanceof ItemColorArmor){
                        ((ItemColorArmor) armor).setColor(((Armor) item).getRgb());
                    }
                    sender.getInventory().setItemInHand(armor);
                    sender.getInventory().addItem(stone.toItem());
                    sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    sender.sendMessage("§r§e[镶嵌系统]§a宝石拆除成功");
                    sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                }else{
                    sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    sender.sendMessage("§r§e[镶嵌系统]§c抱歉，你盔甲没有 "+stoneName+"这个宝石");
                    sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                }
            }else{
                sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                sender.sendMessage("§r§e[镶嵌系统]§c请手持武器 或 盔甲");
                sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            }
        }else{
            sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            sender.sendMessage("§r§e[镶嵌系统]§c抱歉，不存在"+stoneName+"这个宝石 ");
            sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
        }
    }
}
