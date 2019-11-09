package be.griffinbabe.webauctionhouse.events;

import be.griffinbabe.webauctionhouse.Main;
import be.griffinbabe.webauctionhouse.database.DBConnection;
import be.griffinbabe.webauctionhouse.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * This class handles all the sign events in game.
 * Signs are used to determine which
 */
public class SignChessEvents implements Listener {

    private Main plugin;

    private static String WAH_TAG = "[WAH]";
    private static String WAH_SELL = "[SELL]";
    private static String WAH_BUY = "[BUY]";

    private static String SIGN_BUY = "buy";
    private static String SIGN_SELL = "sell";

    private static String ALREADY_BUY_SIGN = Main.CHAT_TAG+"You already have a buy chess.";
    private static String ALREADY_USED_CHESS = Main.CHAT_TAG+"This chess is already in use";
    private static String INTERNAL_ERROR_CREATE = Main.CHAT_TAG+"Internal error, couldn't create chess and/or sign. Please contact an " +
            "administrator";
    private static String BUY_SIGN_CREATED = Main.CHAT_TAG+"Buy sign created";
    private static String SELL_SIGN_CREATED = Main.CHAT_TAG+"Sell sign created";

    private static String SIGN_CHEST_DELETED = Main.CHAT_TAG+"Sign and Chest deleted.";

    private static int SIGN_LINES = 4;


    public SignChessEvents(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles the {@link SignChangeEvent} by checking
     * if the sign is correctly formatted and behind
     * a chess. It will then proceed by calling
     * {@link #createSignAndChess(Block, Block, Player, String)}.
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
                createSignAndChess(sign, chess, e.getPlayer(), SIGN_SELL);
            }
            else if (lines[1].equalsIgnoreCase(WAH_BUY)) {
                createSignAndChess(sign, chess, e.getPlayer(), SIGN_BUY);
            }
            else {
                e.getPlayer().sendMessage(Main.CHAT_TAG+"2nd line must be either [SELL] or [BUY].");
            }
        }
    }

    /**
     * Handles the {@link EntityExplodeEvent} by checking
     * if the block is a {@link Sign} or a {@link Chest}
     * and then calling the adequate function.
     * @param e, the event
     */
    @EventHandler (priority = EventPriority.NORMAL)
    public void explodedSignOrChess(EntityExplodeEvent e) {
        for (Block block : e.blockList()) {
            BlockState state = block.getState();
            if (state instanceof Chest) {
                chestDestroyed((Chest)state, null);
            }
            else if (state instanceof Sign) {
                signDestroyed((Sign)state, null);
            }
        }
    }

    /**
     * Handles the {@link BlockBreakEvent} by checking
     * if the block is a {@link Sign} or a {@link Chest}
     * and then calling the adequate function.
     *
     * @param e, the event.
     */
    @EventHandler (priority = EventPriority.NORMAL)
    public void destroyedSignOrChess(BlockBreakEvent e) {
        BlockState block = e.getBlock().getState();
        Player player = e.getPlayer();
        if (block instanceof Chest) {
            chestDestroyed((Chest)block, player);
        }
        else if (block instanceof Sign) {
            signDestroyed((Sign)block, player);
        }
    }

    /**
     * Handles a deleted chest, by first checking if the chest corresponds
     * to a chest present in the database and then by removing the related
     * chest, sign and items from that database.
     * @param chest, the destroyed {@link Chest}
     * @param player, the {@link Player} that destroyed that chest.
     */
    private void chestDestroyed(Chest chest, Player player) {
        try {
            DBConnection instance = DBConnection.getInstance();
            Long chessId = instance.getChestIdByPosition(chest.getX(), chest.getY(), chest.getZ());
            if (chessId != null) {
                instance.deleteChestAndSign(chessId);
                instance.deleteRelatedChestItems(chessId);
                if (player != null) {
                    player.sendMessage(SIGN_CHEST_DELETED);
                }
            }
        } catch (SQLException e){
            if (player != null) {
                player.sendMessage(Main.INTERNAL_ERROR_MESSAGE);
            }
            e.printStackTrace();
        }

    }

    /**
     * Handles a deleted sign, by first checking if the sign corresponds
     * to a sign present in the database and then by removing the related
     * chest, sign and items from that database.
     * @param sign, the destroyed {@link Sign}
     * @param player, the {@link Player} that destroyed that chest.
     */
    private void signDestroyed(Sign sign, Player player) {
        try {
            DBConnection instance = DBConnection.getInstance();
            Pair<Long,Long> signAndChestIDs = instance.getSignIdByPosition(sign.getX(), sign.getY(), sign.getZ());
            if (signAndChestIDs != null) {
                instance.deleteChestAndSign(signAndChestIDs.second);
                instance.deleteRelatedChestItems(signAndChestIDs.second);
                if (player != null) {
                    player.sendMessage(SIGN_CHEST_DELETED);
                }
            }
        } catch (SQLException e) {
            if (player != null) {
                player.sendMessage(Main.INTERNAL_ERROR_MESSAGE);
            }
            e.printStackTrace();
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
     * Creates a buy/sell chess and sign. It will perform checks as
     * if the chess isn't already registered for another sign, or if
     * the players hasn't already a buy chess if it is a buy chess.
     *
     * @param sign, the sign on the block
     * @param chess, the registered chess
     * @param player, the player that makes the registration.
     */
    public void createSignAndChess(Block sign, Block chess, Player player, String chessType) {
        try {
            String uuid = player.getUniqueId().toString();
            DBConnection instance = DBConnection.getInstance();
            if (chessType.equalsIgnoreCase(SIGN_BUY)) {
                if (instance.checkSignForUsername(uuid, SIGN_BUY)) {
                    // in this case a buy sign for this player has already been created
                    player.sendMessage(ALREADY_BUY_SIGN);
                    sign.breakNaturally();
                    return;
                }
            }
            // check if the chess behind is already taken
            if (instance.getChestIdByPosition(chess.getX(), chess.getY(), chess.getZ()) != null) {
                player.sendMessage(ALREADY_USED_CHESS);
                sign.breakNaturally();
                return;
            }
            Long chessId = instance.insertChess(chess.getX(), chess.getY(), chess.getZ(), uuid);
            if (chessId != null) {
                instance.insertSign(uuid, chessId, sign.getX(), sign.getY(), sign.getZ(), chessType);
                if (chessType.equalsIgnoreCase(SIGN_BUY)) {
                    player.sendMessage(BUY_SIGN_CREATED);
                } else if (chessType.equalsIgnoreCase(SIGN_SELL)) {
                    player.sendMessage(SELL_SIGN_CREATED);
                }
            } else {
                System.out.println(INTERNAL_ERROR_CREATE);
                sign.breakNaturally();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(Main.INTERNAL_ERROR_MESSAGE);
        }

    }


}
