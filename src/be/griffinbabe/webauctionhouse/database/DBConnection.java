package be.griffinbabe.webauctionhouse.database;


import java.sql.*;

/**
 * Communication with sqlite database class.
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

    private static String SIGN_SELL = "sell";
    private static String SIGN_BUY = "buy";

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

    private static DBConnection DBConnection = null;

    private Connection conn = null;

    /**
     * Singleton pattern instance getter. Will the static DBConnection object.
     * If the object hasn't been initialized yet, calls the constructor.
     * @return an {@link DBConnection} object.
     */
    public static DBConnection getInstance() {
        if (DBConnection == null) {
            DBConnection = new DBConnection();
        }
        return DBConnection;
    }

    /**
     * Constructor, will first check if the file has been created.
     * Will then init the tables if they don't already exists.
     */
    private DBConnection() {
        try (Connection conn = DriverManager.getConnection(DATABASE_PATH)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
                initTables();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Initializes the database tables.
     */
    public void initTables() {
        try (Connection conn = DriverManager.getConnection(DATABASE_PATH);
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_PLAYER_TABLE);
            stmt.execute(CREATE_CHESS_TABLE);
            stmt.execute(CREATE_SIGN_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts a player with it's respective uuid.
     * @param uuid, the player uuid.
     */
    public void insertPlayer(String uuid, String username) {
        try (Connection conn = DriverManager.getConnection(DATABASE_PATH);
            PreparedStatement stmt = conn.prepareStatement(INSERT_PLAYER)) {
            stmt.setString(1, uuid);
            stmt.setString(2, username);
            stmt.execute();
            System.out.println("Player with UUID: "+uuid+" inserted to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if there is already a player registered with the
     * given uuid.
     * @param uuid, the player unique id.
     * @return
     */
    public boolean isPlayerRegistered(String uuid) {
        try (Connection conn = DriverManager.getConnection(DATABASE_PATH);
            PreparedStatement stmt = conn.prepareStatement(SEARCH_PLAYER_BY_UUID)){
            stmt.setString(1, uuid);
            ResultSet set = stmt.executeQuery();
            return isQueryEmpty(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a player with a given uuid and an username exists.
     * The function is named like so as you should first call
     * {@link #isPlayerRegistered(String)} before calling this one.
     * @param uuid, the player unique id
     * @param username, the player display name
     * @return
     */
    public boolean havePlayerNameChanged(String uuid, String username) {
        try (Connection conn = DriverManager.getConnection(DATABASE_PATH);
             PreparedStatement stmt = conn.prepareStatement(SEARCH_PLAYER_BY_UUID_AND_USERNAME)) {
            stmt.setString(1, uuid);
            stmt.setString(2, username);
            return isQueryEmpty(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void changePlayerUsername(String uuid, String newName) {
        try (Connection conn = DriverManager.getConnection(DATABASE_PATH);
             PreparedStatement stmt = conn.prepareStatement(SEARCH_PLAYER_BY_UUID_AND_USERNAME)) {
            stmt.setString(1, newName);
            stmt.setString(2, uuid);
            stmt.execute();
            System.out.println("Player with UUID: "+uuid+" changed it's username with: "+newName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a player already have a buy sign attached or not.
     * @param id, the player unique id
     */
    public boolean checkBuySignForUsername(String id) {
        try (Connection conn = DriverManager.getConnection(DATABASE_PATH);
            PreparedStatement stmt = conn.prepareStatement(SEARCH_SIGN_BY_PLAYER_UUID)) {
            stmt.setString(1, id);
            stmt.setString(2, SIGN_BUY);
            return isQueryEmpty(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Function to avoid duplicate code.
     * This function will check if the given {@link PreparedStatement}
     * returns results or not.
     * @param stmt, the statement
     * @return true if there is results, false otherwise.
     * @throws SQLException
     */
    public boolean isQueryEmpty(PreparedStatement stmt) throws SQLException{
        ResultSet set = stmt.executeQuery();
        while (set.next()) {
            return false;
        }
        return true;
    }
}