package be.griffinbabe.webauctionhouse.command;

import be.griffinbabe.webauctionhouse.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpExecutor implements CommandExecutor {

    /** Plugin main class */
    private Main plugin;

    public HelpExecutor(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage("You fired the help command!");
        return true;
    }
}
