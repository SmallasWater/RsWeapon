package weapon.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import weapon.events.PlayerClickGemStoneEvent;
import weapon.events.PlayerRemoveGemStoneEvent;
import weapon.items.BaseItem;
import weapon.items.GemStone;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


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
            Item itemHand = ((Player) commandSender).getInventory().getItemInHand();
            switch (args.get(0)){
                case "help":
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    commandSender.sendMessage("/click add <宝石名称> 给手持的§c武器/盔甲§a镶嵌宝石");
                    commandSender.sendMessage("/click remove <宝石名称> 给手持的§c武器/盔甲§a拆除宝石宝石");
                    commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    break;
                case "add":
                    if(args.size() > 1) {
                        if ("".equals(args.get(1))) {
                            commandSender.sendMessage("§c指令错误，请输入/click help 查看");
                            return false;
                        }
                        BaseItem item = BaseItem.getBaseItem(itemHand);
                        if (item != null) {
                            inviteStone((Player) commandSender, args.get(1), item);
                        }

                    }else{
                        commandSender.sendMessage("§c指令错误，请输入/click help 查看");
                        return false;
                    }

                    break;
                case "remove":
                    if(args.size() > 1) {
                        if ("".equals(args.get(1))) {
                            commandSender.sendMessage("§c指令错误，请输入/click help 查看");
                            return false;
                        }
                        BaseItem item = BaseItem.getBaseItem(itemHand);
                        if (item != null && !item.isGemStone()) {
                            removeStone((Player) commandSender, args.get(1), item);
                        } else {
                            commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                            commandSender.sendMessage("§r§e[镶嵌系统]§c请手持武器 或 盔甲");
                            commandSender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        }
                    }else{
                        commandSender.sendMessage("§c指令错误，请输入/click help 查看");
                        return false;
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
                            remove.setCount(1);
                        }
                        player.getInventory().removeItem(remove);
                        return;
                    }
                }
            }
        }
    }


    private boolean canRemoveItem(Player player, String itemName){
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
        if(canRemoveItem(sender,stoneName)){
            GemStone stone = GemStone.getInstance(stoneName);
            if(item != null && !item.isGemStone()){
                if(stone != null) {
                    if (!item.canUse(sender)) {
                        sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        sender.sendMessage("§r§e[镶嵌系统]§c此武器无法镶嵌宝石");
                        sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    }
                    if (item.canInlay(stone)) {
                        if (new Random().nextInt(100) <= stone.getCg()) {
                            if (item.inlayStone(stone)) {
                                sender.getInventory().setItemInHand(item.toItem());
                                Item st = stone.toItem();
                                st.setCount(1);
                                sender.getInventory().removeItem(stone.toItem());
                                PlayerClickGemStoneEvent events = new PlayerClickGemStoneEvent(sender, stone, item);
                                Server.getInstance().getPluginManager().callEvent(events);
                                sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                                sender.sendMessage("§r§e[镶嵌系统]§e恭喜   宝石镶嵌成功  ");
                                sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                                return;
                            }
                        }
                        sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        sender.sendMessage("§r§e[镶嵌系统]§c 宝石镶嵌失败，， 宝石破碎");
                        sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        sender.getLevel().addSound(sender, Sound.LEASHKNOT_BREAK);
                        toRemoveStone(stone, sender);
                    } else {
                        sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        sender.sendMessage("§r§e[镶嵌系统]§c宝石无法镶嵌");
                        sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    }
                }else {
                    sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    sender.sendMessage("§r§e[镶嵌系统]§c抱歉，你背包并没有"+stoneName+"这个宝石 ");
                    sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                }

            }else{
                sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                sender.sendMessage("§r§e[镶嵌系统]§c请手持 武器 或者 盔甲");
                sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
            }
        }
    }

    private void removeStone(Player sender, String stoneName, BaseItem item){
        GemStone stone = GemStone.getInstance(stoneName);
        if(stone != null){
            if(item != null &&!item.isGemStone()){
                if(item.canRemove(stone)){
                    if(item.removeStone(stone)){
                        sender.getInventory().setItemInHand(item.toItem());
                        sender.getInventory().addItem(stone.toItem());
                        PlayerRemoveGemStoneEvent events = new PlayerRemoveGemStoneEvent(sender,stone,item);
                        Server.getInstance().getPluginManager().callEvent(events);
                        sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        sender.sendMessage("§r§e[镶嵌系统]§a宝石拆除成功");
                        sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    }else{
                        sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                        sender.sendMessage("§r§e[镶嵌系统]§a宝石拆除失败");
                        sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    }
                }else{
                    sender.sendMessage("§r§c▂§6▂§e▂§a▂§b▂§a▂§e▂§6▂§c▂");
                    sender.sendMessage("§r§e[镶嵌系统]§c抱歉，此装备没有"+stoneName+"这个宝石");
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
