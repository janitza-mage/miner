package name.martingeisse.miner.server.persistence;

import name.martingeisse.miner.server.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseImpl implements Database {

    private final String url;
    private final String username;
    private final String password;

    public DatabaseImpl() {
        var configuration = Configuration.get();
        this.url = configuration.databaseUrl();
        this.username = configuration.databaseUsername();
        this.password = configuration.databasePassword();
    }

    @Override
    public Connection newConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

}
