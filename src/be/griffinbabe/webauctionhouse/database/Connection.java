package be.griffinbabe.webauctionhouse.database;

public class Connection {

    private static Connection connection = null;

    public static Connection getInstance() {
        if (connection == null) {
            connection = new Connection();
        }
        return connection;
    }

    private Connection() {

    }
}
