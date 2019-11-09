package be.griffinbabe.webauctionhouse.events;

import be.griffinbabe.webauctionhouse.Main;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

public class ItemEvents implements Listener {

    private Main plugin;

    public ItemEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent e) {
        e.getPlayer().sendMessage("You closed your inventory!");
        if (e.getInventory().getType() == InventoryType.CHEST) {
            e.getPlayer().sendMessage("That's a chest");
        }
        Location l  = e.getInventory().getLocation();
        e.getPlayer().sendMessage(l.getBlockX()+" "+l.getBlockY()+" "+l.getBlockZ());
    }
}
