package hu.nye.progtech.persistence;

import java.sql.*;

public class DatabaseManager {
    private static final String JDBC_URL = "jdbc:h2:./data/amoba_db;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public DatabaseManager() {
        init();
    }

    private void init() {
        try (Connection c = getConnection();
             Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS players (id IDENTITY PRIMARY KEY, name VARCHAR(255) UNIQUE, wins INT)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
}
