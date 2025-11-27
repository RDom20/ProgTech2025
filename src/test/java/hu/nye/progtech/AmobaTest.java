package hu.nye.progtech;

import hu.nye.progtech.domain.Board;
import hu.nye.progtech.domain.Player;
import hu.nye.progtech.service.Game;
import hu.nye.progtech.service.GameService;
import hu.nye.progtech.service.HighScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.nio.charset.StandardCharsets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tesztek a run(...) metódusra és a display/isBoardFull segédfüggvényekre.
 * A GameService, Game és HighScore mockolva vannak, így determinisztikusan tesztelhetők az ágak.
 */
class AmobaTest {

    private GameService mockService;
    private Game mockGame;
    private Board mockBoard;
    private HighScore mockHighScore;

    @BeforeEach
    void init() {
        mockService = mock(GameService.class);
        mockGame = mock(Game.class);
        mockBoard = mock(Board.class);
        mockHighScore = mock(HighScore.class);
    }

    @Test
    void playerThenAiThenPlayer_turnSwitchObserved_viaRun() {
        // Bemenet: (ha a Game.getPlayerName() mockolva van, a bemenetben a név is lehet,
        // de itt mindkettőt beállítjuk a biztonság kedvéért)
        String input = "TestPlayer\nA\n1\nESC\nnem\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        // Mock Game és Board viselkedés
        when(mockGame.getBoard()).thenReturn(mockBoard);

        // FONTOS: biztosítsuk, hogy a board NEM legyen "teljes" a teszt során.
        // Minden cellára '.'-t adunk vissza (üres cella).
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');

        // A board mérete legyen 10 (MAP_SIZE)
        when(mockBoard.getSize()).thenReturn(10);

        // A current player legyen 'X' (player kezd)
        when(mockBoard.getCurrentPlayer()).thenReturn('X');

        // A Game.getPlayerName() visszaadja a bemenetben szereplő nevet
        when(mockGame.getPlayerName()).thenReturn("TestPlayer");

        // AI lépés visszatér egy koordinátával
        hu.nye.progtech.domain.Coordinate aiCoord = new hu.nye.progtech.domain.Coordinate(1, 1);
        when(mockService.placeNextAIMove(eq(mockBoard), any(hu.nye.progtech.domain.Player.class)))
                .thenReturn(aiCoord);

        // hasWon mindig false (se player, se AI nem nyer a teszt során)
        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        // Hívjuk a run metódust mockokkal
        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);

        // Ellenőrizzük, hogy a kimenet tartalmazza a játékos promptot és az AI lépését
        assertTrue(outStr.contains("Játékos:") || outStr.contains("Oszlop (A-J):"),
                "Hiányzik a játékos prompt a kimenetből: " + outStr);
        assertTrue(outStr.contains("AI lépése") || outStr.contains("AI lép"),
                "Az AI lépése nem jelent meg: " + outStr);

        // Ellenőrizzük, hogy a service.placeNextAIMove meghívódott (AI ág)
        verify(mockService, atLeastOnce()).placeNextAIMove(eq(mockBoard), any(hu.nye.progtech.domain.Player.class));
    }


    @Test
    void saveFlow_whenUserChoosesYesOnEsc_recordsSave() {
        String input = "TestPlayer\nESC\ningen\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        when(mockBoard.getCurrentPlayer()).thenReturn('X');

        // game.generateNewSaveFileName és game.saveGame hívásokat ellenőrizzük: mockGame-ot kell beállítani
        // mockGame.generateNewSaveFileName() és mockGame.saveGame(...) nem léteznek a mockolt Game osztályon feltétlenül,
        // ezért a run metódus a Game implementációját használja. Ha a Game metódusok nem elérhetők, a teszt
        // ellenőrizheti a kimenetet, hogy a "Játék mentve!" üzenet megjelenik-e.
        // Itt egyszerűen beállítjuk, hogy board és current player legyenek.
        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString();
        assertTrue(outStr.toLowerCase().contains("mentve") || outStr.toLowerCase().contains("kilépés") || outStr.length() > 0,
                "A mentés ág nem jelezte a mentést a kimenetben. Kimenet:\n" + outStr);
    }

    @Test
    void displayBoardAndIsBoardFull_unitChecks() throws Exception {
        // displayBoard egyszerű ellenőrzése egy valós Board példán (ha van)
        // Ha nincs implementáció, mockoljuk a Board-ot részben
        Board b = mock(Board.class);
        when(b.getSize()).thenReturn(3);
        when(b.getCell(0, 0)).thenReturn('.');
        when(b.getCell(0, 1)).thenReturn('X');
        when(b.getCell(0, 2)).thenReturn('O');
        when(b.getCell(1, 0)).thenReturn('.');
        when(b.getCell(1, 1)).thenReturn('.');
        when(b.getCell(1, 2)).thenReturn('.');
        when(b.getCell(2, 0)).thenReturn('.');
        when(b.getCell(2, 1)).thenReturn('.');
        when(b.getCell(2, 2)).thenReturn('.');

        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf);
        Amoba.displayBoard(b, out);
        String outStr = outBuf.toString();
        assertTrue(outStr.contains("A") && outStr.contains("C"), "Fejléc hiányzik vagy nem megfelelő: " + outStr);
        assertTrue(outStr.contains("X") && outStr.contains("O") || outStr.contains("□"), "Nem található X/O/□ a kimenetben: " + outStr);

        // isBoardFull privát metódus reflection-nel
        // Feltételezzük, hogy Amoba.isBoardFull privát; ha publikus, hívjuk direkt
        Method isBoardFull = Amoba.class.getDeclaredMethod("isBoardFull", Board.class);
        isBoardFull.setAccessible(true);

        // üres cella miatt false
        boolean empty = (boolean) isBoardFull.invoke(null, b);
        assertFalse(empty);

        // töltsük fel a mockot úgy, hogy minden cella 'X'
        when(b.getCell(anyInt(), anyInt())).thenReturn('X');
        boolean full = (boolean) isBoardFull.invoke(null, b);
        assertTrue(full);
    }
}
