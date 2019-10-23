package be.griffinbabe.webauctionhouse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin {

    private static final String HELP_COMMAND = "helpwah";

    public static void main(String[] args) {

    }

    @Override
    public void onEnable() {
        Logger l = getLogger();
        l.info("WAH plugin loaded");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        Logger l = getLogger();
        l.info("WAH plugin unloaded");
        super.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(HELP_COMMAND)) {
            sender.sendMessage("Heyy no help xd");
            return true;
        }
        return false;
    }
}
