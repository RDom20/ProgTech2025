package hu.nye.progtech.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GameTest {
    private static final Logger logger = LoggerFactory.getLogger(GameTest.class);
    private Game game;

    @BeforeEach
    void setup() {
        game = new Game();
        logger.info("Új játék inicializálva.");
    }

    @Test
    void testStartNewGame() {
        logger.info("Teszt: új játék indítása.");
        game.startNewGame();

        Assertions.assertNotNull(game.getBoard());
        Assertions.assertEquals('X', game.getCurrentPlayerSymbol());
        Assertions.assertEquals(10, game.getBoard().getSize());
    }

    @Test
    void testSaveGame() throws IOException {
        game.setPlayerName("TestPlayer");
        game.startNewGame();

        game.makeMove(0, 0); // X lépése
        game.makeMove(0, 1); // O lépése

        // használjuk a Game.getSaveFilePath-et, hogy biztosan ugyanoda nézzünk, ahova a Game ment
        String saveFilePath = game.getSaveFilePath("test_save.md");
        game.saveGame("test_save.md");

        File savedGameFile = new File(saveFilePath);
        Assertions.assertTrue(savedGameFile.exists(), "A mentett fájl nem található!");

        // Az állapot ellenőrzése fájlból olvasva
        try (BufferedReader reader = new BufferedReader(new FileReader(savedGameFile))) {
            String line;

            // Ellenőrizzük a játékos nevét
            line = reader.readLine();
            Assertions.assertTrue(line.contains("Felhasználó: TestPlayer"), "A játékos neve nem megfelelő.");

            // Ellenőrizzük az aktuális játékos szimbólumát
            line = reader.readLine();
            Assertions.assertTrue(line.contains("Aktuális játékos: X"), "A játékos szimbóluma nem megfelelő.");

            // Ellenőrizzük a lépéseket
            line = reader.readLine();
            Assertions.assertTrue(line.contains("X játékos lépései: A1"), "X lépései nem megfelelőek.");
            line = reader.readLine();
            Assertions.assertTrue(line.contains("O játékos lépései: B1"), "O lépései nem megfelelőek.");
        }

        // A fájl törlése
        boolean isDeleted = savedGameFile.delete();
        if (!isDeleted) {
            logger.error("A mentett fájl törlése nem sikerült!");
        } else {
            logger.info("A mentett fájl sikeresen törölve.");
        }
    }

    @Test
    void testLoadGame() throws IOException {
        game.setPlayerName("TestPlayer");
        game.startNewGame();
        game.makeMove(0, 0);  // X lépése
        game.makeMove(0, 1);  // O lépése

        String saveFilePath = game.getSaveFilePath("test_save.md");
        game.saveGame("test_save.md");

        Game loadedGame = new Game();
        loadedGame.loadGame(saveFilePath);

        // Ellenőrizzük, hogy a betöltött játékos neve megegyezik
        Assertions.assertEquals(game.getPlayerName(), loadedGame.getPlayerName());

        // Ellenőrizzük, hogy a betöltött játékos szimbóluma is X
        Assertions.assertEquals('X', loadedGame.getCurrentPlayerSymbol());

        // Ellenőrizzük, hogy a lépések is megfelelően lettek betöltve
        Assertions.assertEquals('X', loadedGame.getBoard().getCell(0, 0)); // X lépése
        Assertions.assertEquals('O', loadedGame.getBoard().getCell(0, 1)); // O lépése

        // töröljük a mentést
        File f = new File(saveFilePath);
        if (f.exists()) {
            boolean deleted = f.delete();
            if (!deleted) {
                logger.warn("Nem sikerült törölni a teszt mentést: {}", saveFilePath);
            }
        }
    }

    @Test
    void testSaveAndDeleteTempFile() throws Exception {
        Game g = new Game();
        g.setPlayerName("Tmp");
        g.startNewGame();
        g.makeMove(0,0);
        String path = g.getSaveFilePath("tmp_test_save.md");
        g.saveGame("tmp_test_save.md");
        File f = new File(path);
        assertTrue(f.exists());
        boolean deleted = f.delete();
        assertTrue(deleted || !f.exists());
    }

    @Test
    void saveCreatesFileAndLoadWorks() throws Exception {
        Game g = new Game();
        g.setPlayerName("IOTest");
        g.startNewGame();
        g.makeMove(0,0);
        String path = g.getSaveFilePath("iotest.md");
        g.saveGame("iotest.md");
        File f = new File(path);
        assertTrue(f.exists());
        Game loaded = new Game();
        loaded.loadGame(path);
        assertEquals(g.getPlayerName(), loaded.getPlayerName());
        // cleanup
        f.delete();
    }

    @Test
    void saveAndLoadRoundtrip() throws IOException {
        Game g = new Game();
        g.setPlayerName("T");
        g.startNewGame();
        g.makeMove(0,0); // X
        g.makeMove(0,1); // O
        String path = g.getSaveFilePath("tmp_test.md");
        g.saveGame("tmp_test.md");

        Game loaded = new Game();
        loaded.loadGame(path);
        assertEquals(g.getPlayerName(), loaded.getPlayerName());
        assertEquals('X', loaded.getBoard().getCell(0,0));
        // cleanup
        new File(path).delete();
    }

}
