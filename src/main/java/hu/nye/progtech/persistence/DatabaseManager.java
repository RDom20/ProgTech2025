package hu.nye.progtech.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager — egyszerű H2 kapcsolatkezelő.
 * Alapértelmezett konstruktor in-memory adatbázist hoz létre (tesztekhez).
 * További konstruktor lehetővé teszi külső JDBC URL / user / password megadását.
 */
public class DatabaseManager {

    private final String jdbcUrl;
    private final String user;
    private final String password;

    /**
     * Alapértelmezett konstruktor — in-memory adatbázis, DB_CLOSE_DELAY=-1,
     * így a tesztek futása alatt az adatbázis megmarad.
     */
    public DatabaseManager() {
        this("jdbc:h2:mem:amoba_db;DB_CLOSE_DELAY=-1", "sa", "");
    }

    /**
     * Konstruktor, ha külső JDBC paramétereket szeretnél megadni (hasznos tesztekhez vagy konfigurációhoz).
     *
     * @param jdbcUrl  JDBC URL
     * @param user     felhasználónév
     * @param password jelszó
     */
    public DatabaseManager(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        init();
    }

    private void init() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS players (id IDENTITY PRIMARY KEY, name VARCHAR(255) UNIQUE, wins INT)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, user, password);
    }
}
