package be.griffinbabe.webauctionhouse.events;

import be.griffinbabe.webauctionhouse.Main;
import be.griffinbabe.webauctionhouse.database.DBConnection;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.logging.Logger;

/**
 * This class handles all the sign events in game.
 * Signs are used to determine which
 */
public class SignEvents implements Listener {

    private Main plugin;

    private static String WAH_TAG = "[WAH]";
    private static String WAH_SELL = "[SELL]";
    private static String WAH_BUY = "[BUY]";

    private static String SIGN_BUY = "buy";
    private static String SIGN_SELL = "sell";

    private static String ALREADY_BUY_SIGN = "You already have a buy chess.";
    private static String ALREADY_USED_CHESS = "This chess is already in use";

    private static int SIGN_LINES = 4;

    public SignEvents(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles the {@link SignChangeEvent} by checking
     * if the sign is correctly formatted and behind
     * a chess. It will then proceed by calling
     * {@link #createSellChess} and {@link #createBuyChess}.
     *
     * @param e, the event.
     */
    @EventHandler (priority = EventPriority.NORMAL)
    public void changeSign(SignChangeEvent e) {
        Logger log = plugin.getLogger();
        String lines[] = e.getLines();
        if (lines[0].equalsIgnoreCase(WAH_TAG)) {
            Block sign = e.getBlock();
            Block chess = detectChess(sign);
            if (chess == null) {
                e.getPlayer().sendMessage(Main.CHAT_TAG+"Sign must be attached on a chess.");
                return;
            }
            String playerName = lines[2];
            if (!playerName.equalsIgnoreCase(e.getPlayer().getName())) {
                log.info(e.getPlayer().getName());
                e.getPlayer().sendMessage(Main.CHAT_TAG+"3rd line must contain your username.");
                return;
            }
            if (lines[1].equalsIgnoreCase(WAH_SELL)) {
                createSellChess(sign, chess, e.getPlayer());
            }
            else if (lines[1].equalsIgnoreCase(WAH_BUY)) {
                createBuyChess(sign, chess, e.getPlayer());
            }
            else {
                e.getPlayer().sendMessage(Main.CHAT_TAG+"2nd line must be either [SELL] or [BUY].");
            }
        }
    }

    /**
     * Checks if the given block is a wallsign and returns
     * the attached chest. If there is no chest, then the
     * function returns null.
     *
     * @param block, the sign we wan to check
     * @return the attached chest, or null if there is
     *      no chest behind our sign.
     */
    private Block detectChess(Block block) {
        if (block.getState() instanceof Sign) {
            BlockData data = block.getBlockData();
            if (data instanceof Directional) {
                Directional directional = (Directional)data;
                Block blockBehind = block.getRelative(directional.getFacing().getOppositeFace());
                if (blockBehind.getState() instanceof Chest) {
                    return blockBehind;
                }
            }
        }
        return null;
    }


    /**
     * Creates a sell chess, storing the chess data in our database.
     *
     * @param sign, the sign on the block
     * @param chess, the registered chess
     * @param player, the player that makes the registration.
     */
    public void createSellChess(Block sign, Block chess, Player player) {
        // TODO: Write this function
        DBConnection instance = DBConnection.getInstance();

    }

    /**
     * Checks first if there in no buy chess already assigned for this player.
     * If there is already one, it will destroy the sign and tell where is existing chess.
     * If there is no chess, it will add it to.
     *
     * @param sign, the sign on the block
     * @param chess, the registered chess
     * @param player, the player that makes the registration.
     */
    public void createBuyChess(Block sign, Block chess, Player player) {
        String uuid = player.getUniqueId().toString();
        DBConnection instance = DBConnection.getInstance();
        if (instance.checkSignForUsername(uuid, SIGN_BUY)) {
            // in this case a buy sign for this player has already been created
            player.sendMessage(ALREADY_BUY_SIGN);
            sign.breakNaturally();
            return;
        }
        // check if the chess behind is already taken
        if (instance.isChessRegistered(chess.getX(), chess.getY(), chess.getZ())) {
            player.sendMessage(ALREADY_USED_CHESS);
            sign.breakNaturally();
            return;
        }
        int chessId = instance.insertChess(chess.getX(), chess.getY(), chess.getZ(), uuid);
        instance.insertSign(uuid, chessId, sign.getY(), sign.getY(), sign.getZ(), SIGN_BUY);


    }


}
