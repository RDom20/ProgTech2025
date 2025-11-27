package hu.nye.progtech.service;

import hu.nye.progtech.persistence.DatabaseManager;
import hu.nye.progtech.persistence.PlayerDao;
import hu.nye.progtech.persistence.PlayerRecord;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HighScoreTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void recordWinIncrementsWinsAndDisplayShowsIt() {
        // in-memory DB egyedi névvel, hogy izolált legyen
        DatabaseManager db = new DatabaseManager("jdbc:h2:mem:hs_test;DB_CLOSE_DELAY=-1", "sa", "");
        PlayerDao dao = new PlayerDao(db);
        HighScore hs = new HighScore(dao);

        // rögzítünk néhány győzelmet (használjuk a String overload-ot)
        hs.recordWin("Tester");
        hs.recordWin("Tester");
        hs.recordWin("Other");

        // lekérdezzük a toplistát közvetlenül a DAO-n keresztül
        List<PlayerRecord> top = dao.getHighScores(10);
        assertFalse(top.isEmpty());
        assertEquals("Tester", top.get(0).getName());
        assertEquals(2, top.get(0).getWins());

        // displayHighScores kiíratása és ellenőrzése
        HighScore.displayHighScores(hs, 10);
        String output = outContent.toString();
        assertTrue(output.contains("Tester"));
        assertTrue(output.contains("2"));
    }

    @Test
    void displayHighScoresHandlesEmptyListGracefully() {
        DatabaseManager db = new DatabaseManager("jdbc:h2:mem:hs_empty;DB_CLOSE_DELAY=-1", "sa", "");
        PlayerDao dao = new PlayerDao(db);
        HighScore hs = new HighScore(dao);

        // nincs rögzített győzelem -> üres lista
        HighScore.displayHighScores(hs, 5);
        String output = outContent.toString();
        // legalább a fejléc vagy valami kimenet legyen
        assertTrue(output.length() > 0);
    }
}
