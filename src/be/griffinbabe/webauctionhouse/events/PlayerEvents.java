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
        String username = e.getPlayer().getDisplayName();
        DBConnection instance = DBConnection.getInstance();
        if (!instance.isPlayerRegistered(id)) {
            instance.insertPlayer(id, username);
            System.out.println("New player added to database with username: "+username);
        } else if (instance.havePlayerNameChanged(id, username)) {
            // if the player has changed it's username, will change the username in the database
            // and also change the username in all it's signs
            instance.changePlayerUsername(id, username);
            updateSignsNames(id, username);
        }
    }

    /**
     * Updates all the signs on the map with the new player's username
     *
     * @param id
     * @param newName
     */
    public void updateSignsNames(String id, String newName) {
        // TODO: Write this function
    }

}
