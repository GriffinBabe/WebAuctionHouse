package be.griffinbabe.webauctionhouse;

import be.griffinbabe.webauctionhouse.command.AvailableCommands;
import be.griffinbabe.webauctionhouse.command.HelpExecutor;
import be.griffinbabe.webauctionhouse.events.SignEvents;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin {

    public static String CHAT_TAG = ChatColor.YELLOW+"[WebAuctionHouse]"+ChatColor.WHITE+" ";

    public static void main(String[] args) {

    }

    @Override
    public void onEnable() {
        Logger l = getLogger();
        l.info("Plugin loaded.");

        try {
            this.getCommand(AvailableCommands.HELP.name)
                    .setExecutor(new HelpExecutor(this));
        } catch (NullPointerException e) {
            l.info("Can't load command executor for a command. "+
                    "Please check if the command is properly defined in the plugin.yml file." );
        }

        getServer().getPluginManager().registerEvents(new SignEvents(this), this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        Logger l = getLogger();
        l.info("Plugin disabled.");
        super.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

}
