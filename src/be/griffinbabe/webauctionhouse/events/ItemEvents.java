package be.griffinbabe.webauctionhouse.events;

import be.griffinbabe.webauctionhouse.Main;
import be.griffinbabe.webauctionhouse.database.DBConnection;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class ItemEvents implements Listener {

    private Main plugin;

    public ItemEvents(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        if (inventory.getType() == InventoryType.CHEST) {
            e.getPlayer().sendMessage("You closed a chest");
            Location location = inventory.getLocation();
            try {
                DBConnection instance = DBConnection.getInstance();
                Long isWahChest = instance.getChestIdByPosition
                        (location.getBlockX(), location.getBlockY(), location.getBlockZ());
                if (isWahChest == null) return;
                e.getPlayer().sendMessage("This is a WebAuctionHouse chest");
                // stacks represent all the items contained by the chess
                ItemStack[] stacks = inventory.getContents();
                for (ItemStack stack : stacks) {
                    // checks the lore of the item data to see if it's identified
                    String stackid = stack.getItemMeta().getLore().get(0);
                    instance.checkItemId(stackid);

                    // String itemid = stack.getItemMeta().get
                }
            } catch (SQLException exc) {
                exc.printStackTrace();
                e.getPlayer().sendMessage(Main.INTERNAL_ERROR_MESSAGE);
            }
        }
    }

}
