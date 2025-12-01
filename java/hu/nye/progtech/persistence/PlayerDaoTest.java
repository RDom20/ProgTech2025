package hu.nye.progtech.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlayerDaoTest {
    private static final Logger logger = LoggerFactory.getLogger(PlayerDaoTest.class);

    private DatabaseManager dbManager;
    private PlayerDao dao;

    @BeforeEach
    void setUp() {
        String dbName = "testdb_" + UUID.randomUUID() + "_" + System.nanoTime();
        String jdbcUrl = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1";
        dbManager = new DatabaseManager(jdbcUrl, "sa", "");
        dao = new PlayerDao(dbManager);
    }

    @AfterEach
    void tearDown() {
        dbManager = null;
        dao = null;
    }

    @Test
    void testUpsertWinAndGetHighScores() {
        List<PlayerRecord> empty = dao.getHighScores(10);
        assertNotNull(empty);
        assertTrue(empty.isEmpty());

        dao.upsertWin("Alice");
        dao.upsertWin("Alice");
        dao.upsertWin("Bob");

        List<PlayerRecord> top = dao.getHighScores(10);
        assertNotNull(top);
        assertTrue(top.size() >= 2);

        PlayerRecord first = top.get(0);
        assertEquals("Alice", first.getName());
        assertEquals(2, first.getWins());

        boolean foundBob = top.stream().anyMatch(p -> "Bob".equals(p.getName()) && p.getWins() == 1);
        assertTrue(foundBob);

        try (Connection c = dbManager.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT name, wins FROM players ORDER BY wins DESC, name ASC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                logger.info("DB ROW: {} -> {}", rs.getString("name"), rs.getInt("wins"));
            }
        } catch (Exception e) {
            // ignore debug dump errors in test
        }
    }

    @Test
    void testGetHighScoresLimit() {
        for (int i=1;i<=5;i++) for (int j=0;j<i;j++) dao.upsertWin("P"+i);
        List<PlayerRecord> top3 = dao.getHighScores(3);
        assertNotNull(top3);
        assertEquals(3, top3.size());
        assertTrue(top3.get(0).getWins() >= top3.get(1).getWins());
        assertTrue(top3.get(1).getWins() >= top3.get(2).getWins());
    }
}
