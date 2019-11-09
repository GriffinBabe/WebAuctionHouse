package be.griffinbabe.webauctionhouse.command;

import be.griffinbabe.webauctionhouse.Main;
import be.griffinbabe.webauctionhouse.database.DBConnection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class ResetDBExecutor implements CommandExecutor {

    private static String SUCCESS = "Database reseted, please log again";
    private static String INTERNAL_ERROR = "Internal error, couldn't reset database";

    private Main plugin;

    private static String INTERNAL_ERROR = "Internal SQLException, can't reset database";

    public ResetDBExecutor(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        try {
            DBConnection connection = DBConnection.getInstance();
            connection.resetDB();
            commandSender.sendMessage(SUCCESS);
        } catch (SQLException e) {
            e.printStackTrace();
            commandSender.sendMessage(INTERNAL_ERROR);
        }
        return false;
    }
}
