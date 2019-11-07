package be.griffinbabe.webauctionhouse.command;

import be.griffinbabe.webauctionhouse.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetDBExecutor implements CommandExecutor {

    private Main plugin;

    public ResetDBExecutor(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}
