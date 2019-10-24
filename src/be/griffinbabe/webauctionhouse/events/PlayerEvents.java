package be.griffinbabe.webauctionhouse.events;

import be.griffinbabe.webauctionhouse.Main;
import be.griffinbabe.webauctionhouse.database.DBConnection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

/**
 * This class handles all the events relative
 * to player actions such as connection, disconnection, ...
 */
public class PlayerEvents implements Listener {

    private Main plugin;

    public PlayerEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void playerLogin(PlayerLoginEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        String id = uuid.toString();
        DBConnection instance = DBConnection.getInstance();
        if (!instance.isPlayerRegistered(id)) {
            instance.insertPlayer(id);
        }
    }

}
