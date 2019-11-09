package be.griffinbabe.webauctionhouse;

import be.griffinbabe.webauctionhouse.command.AvailableCommands;
import be.griffinbabe.webauctionhouse.command.HelpExecutor;
import be.griffinbabe.webauctionhouse.command.ResetDBExecutor;
import be.griffinbabe.webauctionhouse.database.DBConnection;
import be.griffinbabe.webauctionhouse.events.ItemEvents;
import be.griffinbabe.webauctionhouse.events.PlayerEvents;
import be.griffinbabe.webauctionhouse.events.SignChessEvents;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    public static String CHAT_TAG = ChatColor.YELLOW+"[WebAuctionHouse]"+ChatColor.WHITE+" ";

    public static String PLUGIN_FOLDER = "WebAuctionHouse/";

    public static String INTERNAL_ERROR_MESSAGE = CHAT_TAG+"Internal SQLException, can't reset database";

    @Override
    public void onEnable() {
        Logger l = getLogger();
        l.info("Plugin loaded.");
        try {
            this.getCommand(AvailableCommands.HELP.name)
                    .setExecutor(new HelpExecutor(this));
            this.getCommand(AvailableCommands.RESETDB.name)
                    .setExecutor(new ResetDBExecutor(this));
        } catch (NullPointerException e) {
            l.info("Can't load command executor for a command. "+
                    "Please check if the command is properly defined in the plugin.yml file." );
        }
        getServer().getPluginManager().registerEvents(new SignChessEvents(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
        getServer().getPluginManager().registerEvents(new ItemEvents(this), this);
        checkDataFiles();
        super.onEnable();
    }

    /**
     * Checks if the WebAuctionHouse plugin folder has been created.
     */
    private void checkDataFiles() {
        this.saveConfig();
        this.saveDefaultConfig();
        // Getting the instance will check if the database has already been initialized
        try {
            DBConnection.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
