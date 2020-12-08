package weapon.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class WeShowCommand extends Command {
    public WeShowCommand(String name) {
        super(name,"显示手持武器的套装效果");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return false;
    }
}
