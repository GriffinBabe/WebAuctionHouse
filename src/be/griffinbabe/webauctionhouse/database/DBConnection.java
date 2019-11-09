package be.griffinbabe.webauctionhouse.database;

import be.griffinbabe.webauctionhouse.util.Pair;
import org.bukkit.util.Vector;

import java.sql.*;

/**
 * Communication with SQLite database class.
*/
public class DBConnection {

    private static String DATABASE_PATH = "jdbc:sqlite:plugins/WebAuctionHouse/database.db";

    private static String SIGN_TABLE = "signs";
    private static String CHESS_TABLE = "chess";

    private static String SIGN_ID = "id";
    private static String SIGN_OWNER = "player_uuid";
    private static String SIGN_CHESS_ID = "chess_id";
    private static String SIGN_X = "x";
    private static String SIGN_Y = "y";
    private static String SIGN_Z = "z";
    private static String SIGN_MODE = "mode";

    private static String CHESS_ID = "id";
    private static String CHESS_OWNER = "player_uuid";
    private static String CHESS_X = "x";
    private static String CHESS_Y = "y";
    private static String CHESS_Z = "z";

    private static String PLAYER_TABLE = "players";
    private static String PLAYER_UUID = "uuid";
    private static String PLAYER_USERNAME = "username";

    private static String CREATE_SIGN_TABLE = "CREATE TABLE IF NOT EXISTS "+SIGN_TABLE+
            "(" + SIGN_ID + " integer PRIMARY KEY, \n" +
            SIGN_OWNER + " text,\n" +
            SIGN_CHESS_ID + " integer, \n" +
            SIGN_X + " integer, " + SIGN_Y + " integer, " + SIGN_Z + " integer, " + SIGN_MODE + " mode text, \n"+
            "foreign key(" + SIGN_CHESS_ID + ") references " + CHESS_TABLE + "(" + CHESS_ID + "),\n" +
            "foreign key(" + SIGN_OWNER + ") references " + PLAYER_TABLE + "(" + PLAYER_UUID + "));";

    private static String CREATE_CHESS_TABLE = "CREATE TABLE IF NOT EXISTS "+CHESS_TABLE+
            "("+CHESS_ID+" integer PRIMARY KEY, \n" +
            CHESS_X+" integer, " + CHESS_Y + " integer, "+CHESS_Z+" integer, \n" +
            CHESS_OWNER+" text, foreign key("+CHESS_OWNER+") references "+PLAYER_TABLE+"("+PLAYER_UUID+"));";

    private static String CREATE_PLAYER_TABLE = "CREATE TABLE IF NOT EXISTS "+PLAYER_TABLE+
            " ("+PLAYER_UUID+" text PRIMARY KEY, \n"+
            PLAYER_USERNAME+" text);";

    private static String INSERT_PLAYER = "insert into "+PLAYER_TABLE+"("+PLAYER_UUID+","+PLAYER_USERNAME+") VALUES(?,?);";

    private static String SEARCH_PLAYER_BY_UUID = "select "+PLAYER_UUID+
            " from "+PLAYER_TABLE+" where "+PLAYER_UUID+"== ?;";

    private static String SEARCH_PLAYER_BY_UUID_AND_USERNAME = "select  "+PLAYER_UUID+
            " from "+PLAYER_TABLE+" where "+PLAYER_UUID+"== ? and "+PLAYER_USERNAME+"== ?;";

    private static String UPDATE_USERNAME = "update "+PLAYER_TABLE+" set "+PLAYER_USERNAME+" = ? where "
            +PLAYER_UUID+" == ?;";

    private static String SEARCH_SIGN_BY_PLAYER_UUID = "select "+SIGN_ID+" from "+SIGN_TABLE+" where "+SIGN_OWNER+" == ?"+
            " and "+SIGN_MODE+" == ?;";

    private static String GET_SIGN_POS_BY_CHESS_ID = "select "+SIGN_X+", "+SIGN_Y+", "+SIGN_Z+" from "+SIGN_TABLE+
            " where "+SIGN_CHESS_ID+" == ?;";

    private static String SEARCH_CHESS_BY_POSITION = "select "+CHESS_ID+" from "+CHESS_TABLE+" where "+CHESS_X+" == ? and "+
            CHESS_Y+" == ? and "+CHESS_Z+" == ?;";

    private static String SEARCH_SIGN_BY_POSITION = "select "+SIGN_ID+", "+SIGN_CHESS_ID+" from "+SIGN_TABLE+" where "+
            SIGN_X+" ==? and "+SIGN_Y+" == ? and "+SIGN_Z+"== ?;";

    private static String INSERT_CHESS = "insert into "+CHESS_TABLE+"("+CHESS_X+","+CHESS_Y+","+CHESS_Z+","+CHESS_OWNER+
            ") VALUES(?,?,?,?);";

    private static String INSERT_SIGN = "insert into "+SIGN_TABLE+"("+SIGN_OWNER+","+SIGN_CHESS_ID+","+SIGN_X+","+
            SIGN_Y+","+SIGN_Z+","+SIGN_MODE+") VALUES (?,?,?,?,?,?);";

    private static String DELETE_SIGN_BY_CHEST_ID = "delete from "+SIGN_TABLE+" where "+SIGN_CHESS_ID+"== ?;";

    private static String DELETE_CHEST_BY_ID = "delete from "+CHESS_TABLE+ " where "+CHESS_ID+"== ?;";

    private static String DROP_TABLE_CHESS = "drop table if exists "+ CHESS_TABLE +";";
    private static String DROP_TABLE_PLAYER = "drop table if exists "+ PLAYER_TABLE +";";
    private static String DROP_TABLE_SIGN = "drop table if exists "+ SIGN_TABLE +";";

    private static DBConnection DBConnection = null;

    /**
     * Singleton pattern instance getter. Will the static DBConnection object.
     * If the object hasn't been initialized yet, calls the constructor.
     * @return an {@link DBConnection} object.
     * @throws SQLException if there is a problem with the database communication
     */
    public static DBConnection getInstance() throws SQLException{
        if (DBConnection == null) {
            DBConnection = new DBConnection();
        }
        return DBConnection;
    }

    /**
     * Constructor, will first check if the file has been created.
     * Will then init the tables if they don't already exists.
     * @throws SQLException if there is a problem with the database communication
     */
    private DBConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        if (conn != null) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("The driver name is " + meta.getDriverName());
            initTables();
        }
    }

    /**
     * Initializes the database tables.
     * @throws SQLException if there is a problem with the database communication
     */
    private void initTables() throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        Statement stmt = conn.createStatement();
        stmt.execute(CREATE_PLAYER_TABLE);
        stmt.execute(CREATE_CHESS_TABLE);
        stmt.execute(CREATE_SIGN_TABLE);
        stmt.close();
    }

    /**
     * Inserts a player with it's respective uuid.
     * @param uuid, the player uuid.
     * @throws SQLException if there is a problem with the database communication
     */
    public void insertPlayer(String uuid, String username) throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        PreparedStatement stmt = conn.prepareStatement(INSERT_PLAYER);
        stmt.setString(1, uuid);
        stmt.setString(2, username);
        stmt.execute();
        System.out.println("Player with UUID: "+uuid+" inserted to database.");
        stmt.close();
    }

    /**
     * Checks if there is already a player registered with the
     * given uuid.
     * @param uuid, the player unique id.
     * @return if the player has been registered or not
     * @throws SQLException if there is a problem with the database communication
     */
    public boolean isPlayerRegistered(String uuid) throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        PreparedStatement stmt = conn.prepareStatement(SEARCH_PLAYER_BY_UUID);
        stmt.setString(1, uuid);
        ResultSet set = stmt.executeQuery();
        boolean val = !isQueryEmpty(stmt);
        stmt.close();
        return val;
    }

    /**
     * Checks if a player with a given uuid and an username exists.
     * The function is named like so as you should first call
     * {@link #isPlayerRegistered(String)} before calling this one.
     * @param uuid, the player unique id
     * @param username, the player display name
     * @return true if the name has been changed, false otherwise
     * @throws SQLException if there is a problem with the database communication
     */
    @SuppressWarnings("Duplicates")
    public boolean havePlayerNameChanged(String uuid, String username) throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        PreparedStatement stmt = conn.prepareStatement(SEARCH_PLAYER_BY_UUID_AND_USERNAME);
        stmt.setString(1, uuid);
        stmt.setString(2, username);
        boolean val = isQueryEmpty(stmt);
        stmt.close();
        return val;
    }

    /**
     * Updates the player username to the database,
     * usually called when a player changes it's username.
     * @param uuid the player uuid (always unchanged)
     * @param newName the player new username
     * @throws SQLException if there is a problem with the database communication
     */
    public void changePlayerUsername(String uuid, String newName) throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        PreparedStatement stmt = conn.prepareStatement(UPDATE_USERNAME);
        stmt.setString(1, newName);
        stmt.setString(2, uuid);
        stmt.execute();
        System.out.println("Player with UUID: "+uuid+" changed it's username with: "+newName);
        stmt.close();
    }

    /**
     * Checks if a player already have a buy sign attached or not.
     * @param id, the player unique id
     * @param signMode, the sign mode, either BUY or SELL
     * @throws SQLException if there is a problem with the database communication
     */
    @SuppressWarnings("Duplicates")
    public boolean checkSignForUsername(String id, String signMode) throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        PreparedStatement stmt = conn.prepareStatement(SEARCH_SIGN_BY_PLAYER_UUID);
        stmt.setString(1, id);
        stmt.setString(2, signMode);
        boolean val = !isQueryEmpty(stmt);
        stmt.close();
        return val;
    }

    /**
     * Function to avoid duplicate code.
     * This function will check if the given {@link PreparedStatement}
     * returns results or not.
     * @param stmt, the statement
     * @return true if there is results, false otherwise.
     * @throws SQLException if there is a problem with the database communication
     */
    private boolean isQueryEmpty(PreparedStatement stmt) throws SQLException{
        ResultSet set = stmt.executeQuery();
        return !set.next();
    }

    /**
     * Checks if the chess is already registered to the database,
     * meaning that the chess is already used by a sign.
     *
     * @param x chess x position
     * @param y chess y position
     * @param z chess z position
     * @return true if the player is already registered, false otherwise
     * @throws SQLException if there is a problem with the database communication
     */
    public Long getChestIdByPosition(int x, int y, int z) throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        PreparedStatement stmt = conn.prepareStatement(SEARCH_CHESS_BY_POSITION);
        stmt.setInt(1, x);
        stmt.setInt(2, y);
        stmt.setInt(3, z);
        ResultSet set = stmt.executeQuery();
        if (!set.next()) {
            stmt.close();
            return null;
        }
        else {
            Long result = set.getLong(1);
            stmt.close();
            return result;
        }
    }

    /**
     * Inserts a chess into the database and returns the generated id
     * If an error occurred, this functions returns null
     * @param x, the chess x position
     * @param y, the chess y position
     * @param z, the chess z position
     * @param uuid, the chess owner uuid
     * @return the chess unique key
     * @throws SQLException if there is a problem with the database communication
     */
    public Long insertChess(int x, int y, int z, String uuid) throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        PreparedStatement stmt = conn.prepareStatement(INSERT_CHESS,Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, x);
        stmt.setInt(2, y);
        stmt.setInt(3, z);
        stmt.setString(4, uuid);
        stmt.executeUpdate();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        stmt.close();
        generatedKeys.next();
        return generatedKeys.getLong(1);
    }

    /**
     * Inserts a sign to the database.
     *
     * @param uuid the owner (player) uuid
     * @param chessId the attached chess id
     * @param x the sign x position
     * @param y the sign y position
     * @param z the sign z position
     * @param signMode the sign mode, either BUY or SELL
     * @throws SQLException if there is a problem with the database communication
     */
    public void insertSign(String uuid, Long chessId, int x, int y, int z, String signMode) throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        PreparedStatement stmt = conn.prepareStatement(INSERT_SIGN);
        stmt.setString(1, uuid);
        stmt.setLong(2, chessId);
        stmt.setInt(3, x);
        stmt.setInt(4, y);
        stmt.setInt(5, z);
        stmt.setString(6, signMode);
        stmt.execute();
        stmt.close();
    }


    /**
     * Resets the database tables, dropping them and then
     * calling the {@link #initTables()} function.
     *
     * @throws SQLException if there is a problem with the database communication
     */
    public void resetDB() throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        Statement stmtPlayers = conn.createStatement();
        Statement stmtSigns = conn.createStatement();
        Statement stmtChess = conn.createStatement();
        stmtPlayers.execute(DROP_TABLE_PLAYER);
        stmtPlayers.close();
        stmtSigns.execute(DROP_TABLE_SIGN);
        stmtSigns.close();
        stmtChess.execute(DROP_TABLE_CHESS);
        stmtChess.close();
        initTables();
    }

    /**
     * Returns the position of a sign from the attached chest id.
     *
     * @param chessId, the attached chest id
     * @return the position, in the {@link Vector} data type.
     * @throws SQLException if there is a problem with the database communication.
     */
    public Vector getSignPositionByChessId(Long chessId) throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        PreparedStatement stmt = conn.prepareStatement(GET_SIGN_POS_BY_CHESS_ID);
        stmt.setInt(1, chessId.intValue());
        ResultSet set = stmt.executeQuery();
        if (!set.next()) {
            stmt.close();
            return null;
        } else {
            int x = set.getInt(1);
            int y = set.getInt(2);
            int z = set.getInt(3);
            stmt.close();
            return new Vector(x,y,z);
        }
    }

    /**
     * Deletes a sign and a related chess entry by the chess id.
     * @param chestId, the chess id value
     * @throws SQLException if there is a problem with the database communication.
     */
    public void deleteChestAndSign(Long chestId) throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);

        PreparedStatement stmtSign = conn.prepareStatement(DELETE_SIGN_BY_CHEST_ID);
        stmtSign.setInt(1, chestId.intValue());
        stmtSign.execute();
        stmtSign.close();

        PreparedStatement stmtChest = conn.prepareStatement(DELETE_CHEST_BY_ID);
        stmtChest.setInt(1, chestId.intValue());
        stmtChest.execute();
        stmtChest.close();
    }

    /**
     * Deletes from the database all the items contained in a chest.
     * @param chessId, the related chest id
     */
    public void deleteRelatedChestItems(Long chessId) {
        // TODO Write this function
    }

    @SuppressWarnings("Duplicates")
    public Pair<Long,Long> getSignIdByPosition(int x, int y, int z) throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_PATH);
        PreparedStatement stmt = conn.prepareStatement(SEARCH_SIGN_BY_POSITION);
        stmt.setInt(1, x);
        stmt.setInt(2, y);
        stmt.setInt(3, z);
        ResultSet set = stmt.executeQuery();
        if (!set.next()) {
            stmt.close();
            return null;
        }
        Long signId = set.getLong(1);
        Long chestId = set.getLong(2);
        stmt.close();
        return new Pair<Long, Long>(signId, chestId);
    }
}
