package be.griffinbabe.webauctionhouse.command;

import be.griffinbabe.webauctionhouse.Main;
import be.griffinbabe.webauctionhouse.database.DBConnection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public class ResetDBExecutor implements CommandExecutor {

    private static String SUCCESS_MESSAGE = "Database reseted, please log again";

    private Main plugin;

    public ResetDBExecutor(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        try {
            DBConnection connection = DBConnection.getInstance();
            connection.resetDB();
            commandSender.sendMessage(SUCCESS_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            commandSender.sendMessage(Main.INTERNAL_ERROR_MESSAGE);
        }
        return false;
    }
}
