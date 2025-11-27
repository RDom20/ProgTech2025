package hu.nye.progtech.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    private String jdbcUrlProperty;

    @BeforeEach
    void setUp() {
        String dbName = "testdb_" + UUID.randomUUID();
        jdbcUrlProperty = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1";
        System.setProperty("amoba.jdbc.url", jdbcUrlProperty);
        System.setProperty("amoba.jdbc.user", "sa");
        System.setProperty("amoba.jdbc.password", "");
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("amoba.jdbc.url");
        System.clearProperty("amoba.jdbc.user");
        System.clearProperty("amoba.jdbc.password");
    }

    @Test
    void testInitCreatesPlayersTableAndGetConnection() throws Exception {
        DatabaseManager dbManager = new DatabaseManager();

        try (Connection c = dbManager.getConnection();
             Statement s = c.createStatement()) {

            try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM players")) {
                assertTrue(rs.next());
                int count = rs.getInt(1);
                assertTrue(count >= 0);
            }
        }
    }
}
