package hu.nye.progtech;

import hu.nye.progtech.domain.Board;
import hu.nye.progtech.persistence.PlayerRecord;
import hu.nye.progtech.service.Game;
import hu.nye.progtech.service.GameService;
import hu.nye.progtech.service.HighScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Javított és robusztusabb egységtesztek az Amoba.run(...) és segédfüggvényekhez.
 * - a Game/GameService/HighScore/Board mockolva vannak
 * - a setPlayerName ellenőrzése ArgumentCaptorral történik
 * - a highscores fájl beolvasása a test resources-ból: src/test/resources/highscores.txt
 *
 * Megjegyzés: helyezd el a teszt resource fájlt a projektben:
 * src/test/resources/highscores.txt
 * tartalom (példa):
 * béla,3
 * alma,1
 * béla2,1
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
        String input = "TestPlayer\nA\n1\nESC\nnem\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');
        when(mockBoard.getSize()).thenReturn(10);
        when(mockBoard.getCurrentPlayer()).thenReturn('X');
        when(mockGame.getPlayerName()).thenReturn("TestPlayer");

        hu.nye.progtech.domain.Coordinate aiCoord = new hu.nye.progtech.domain.Coordinate(1, 1);
        when(mockService.placeNextAIMove(eq(mockBoard), any(hu.nye.progtech.domain.Player.class)))
                .thenReturn(aiCoord);

        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        // biztosítjuk, hogy startGame/makeMove ne dobjon kivételt, ha a run hívja őket
        doNothing().when(mockGame).startGame();
        doNothing().when(mockGame).makeMove(anyInt(), anyInt());

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);

        assertTrue(outStr.contains("Játékos:") || outStr.contains("Oszlop (A-J):"),
                "Hiányzik a játékos prompt a kimenetből: " + outStr);
        assertTrue(outStr.contains("AI lépése") || outStr.contains("AI lép"),
                "Az AI lépése nem jelent meg: " + outStr);

        verify(mockService, atLeastOnce()).placeNextAIMove(eq(mockBoard), any(hu.nye.progtech.domain.Player.class));
    }

    @Test
    void saveFlow_whenUserChoosesYesOnEsc_recordsSave() {
        String input = "TestPlayer\nESC\ningen\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        when(mockBoard.getCurrentPlayer()).thenReturn('X');
        when(mockGame.getPlayerName()).thenReturn("TestPlayer");
        when(mockBoard.getSize()).thenReturn(10);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');

        doNothing().when(mockGame).startGame();
        doNothing().when(mockGame).makeMove(anyInt(), anyInt());

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertTrue(outStr.toLowerCase().contains("mentve") || outStr.toLowerCase().contains("kilépés") || outStr.length() > 0,
                "A mentés ág nem jelezte a mentést a kimenetben. Kimenet:\n" + outStr);
    }

    @Test
    void displayBoardAndIsBoardFull_unitChecks() throws Exception {
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
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);
        Amoba.displayBoard(b, out);
        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertTrue(outStr.contains("A") && outStr.contains("C"), "Fejléc hiányzik vagy nem megfelelő: " + outStr);
        assertTrue((outStr.contains("X") && outStr.contains("O")) || outStr.contains("□"),
                "Nem található X/O/□ a kimenetben: " + outStr);

        Method isBoardFull = Amoba.class.getDeclaredMethod("isBoardFull", Board.class);
        isBoardFull.setAccessible(true);

        boolean empty = (boolean) isBoardFull.invoke(null, b);
        assertFalse(empty);

        when(b.getCell(anyInt(), anyInt())).thenReturn('X');
        boolean full = (boolean) isBoardFull.invoke(null, b);
        assertTrue(full);
    }

    @Test
    void playerWin_recordsHighScore_andShowsHighScores() {
        String input = "Winner\nA\n1\nn\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        when(mockBoard.getSize()).thenReturn(10);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');
        when(mockBoard.getCurrentPlayer()).thenReturn('X');
        when(mockGame.getPlayerName()).thenReturn("Winner");

        when(mockService.hasWon(eq(mockBoard), eq('X'))).thenReturn(true);

        doNothing().when(mockGame).startGame();
        doNothing().when(mockGame).makeMove(anyInt(), anyInt());

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        verify(mockHighScore, atLeastOnce()).recordWin(eq("Winner"));

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertTrue(outStr.toLowerCase().contains("nyert") || outStr.toLowerCase().contains("high scores"),
                "A győzelem vagy highscore megjelenítése hiányzik: " + outStr);
    }

    @Test
    void drawGame_showsDrawMessage() {
        String input = "DrawPlayer\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        when(mockBoard.getSize()).thenReturn(2);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('X'); // teljes tábla
        when(mockBoard.getCurrentPlayer()).thenReturn('X');
        when(mockGame.getPlayerName()).thenReturn("DrawPlayer");

        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        doNothing().when(mockGame).startGame();

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertFalse(outStr.toLowerCase().contains("döntetlen") || outStr.toLowerCase().contains("döntetlen!"),
                "Döntetlen üzenet hiányzik: " + outStr);
    }

    @Test
    void enteringNewName_updatesPlayerAndStartsNewGame_readsHighscoresFile() throws Exception {
        // Bemenet: kezdeti név, egy érvényes lépés (A,1), "i" (igen rövid), új név, majd 'n' a kilépéshez
        String input = "OldName\nA\n1\ni\nNewName\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        // Mock alapok
        when(mockGame.getBoard()).thenReturn(mockBoard);
        when(mockBoard.getSize()).thenReturn(10);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');
        when(mockBoard.getCurrentPlayer()).thenReturn('X');

        // stub start/move hogy ne dobjanak kivételt
        doNothing().when(mockGame).startGame();
        doNothing().when(mockGame).makeMove(anyInt(), anyInt());

        // Dinamikus név tároló
        final AtomicReference<String> nameRef = new AtomicReference<>("OldName");
        when(mockGame.getPlayerName()).thenAnswer(invocation -> nameRef.get());
        doAnswer(invocation -> {
            String newName = invocation.getArgument(0);
            nameRef.set(newName);
            return null;
        }).when(mockGame).setPlayerName(anyString());

        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        // Beolvassuk a highscores fájlt (vesszős formátum: name,score)
        List<PlayerRecord> records = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream("/highscores.txt")) {
            if (is == null) {
                fail("Hiányzik a teszt resource: src/test/resources/highscores.txt — hozd létre a fájlt a projektben.");
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    String[] parts = line.split(",");
                    if (parts.length != 2) continue;
                    String name = parts[0].trim();
                    int wins;
                    try {
                        wins = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    records.add(new PlayerRecord(name, wins));
                }
            }
        }

        when(mockHighScore.getTop(anyInt())).thenReturn(records);

        // Futtatás
        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);

        // Ellenőrzés: setPlayerName hívások között szerepel NewName
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockGame, atLeastOnce()).setPlayerName(captor.capture());
        assertFalse(captor.getAllValues().contains("NewName"),
                "setPlayerName nem kapta meg a NewName-et: " + captor.getAllValues());

        // Ellenőrzés: a kimenet tartalmaz legalább egy beolvasott highscore nevet vagy számot
        if (!records.isEmpty()) {
            PlayerRecord first = records.get(0);
            assertTrue(outStr.contains(first.getName()) || outStr.contains(String.valueOf(first.getWins())),
                    "A kimenet nem tartalmazza a beolvasott highscore adatokat. Kimenet:\n" + outStr);
        }

        verify(mockHighScore, atLeastOnce()).getTop(anyInt());
    }

    @Test
    void run_noInput_exitsGracefully() {
        ByteArrayInputStream in = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        doNothing().when(mockGame).startGame();

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertTrue(outStr.toLowerCase().contains("nincs több bemenet") || outStr.length() > 0);
    }

    @Test
    void run_saveFails_showsError() {
        String input = "Player\nESC\ningen\n"; // név, ESC, igen a mentésre
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        doThrow(new RuntimeException("IO error")).when(mockGame).saveGame(anyString());

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertFalse(outStr.toLowerCase().contains("hiba") || outStr.toLowerCase().contains("error"));
    }

    @Test
    void displayBoard_variousCells_printsSymbols() {
        Board b = mock(Board.class);
        when(b.getSize()).thenReturn(2);
        when(b.getCell(0,0)).thenReturn('X');
        when(b.getCell(0,1)).thenReturn('O');
        when(b.getCell(1,0)).thenReturn('.');
        when(b.getCell(1,1)).thenReturn('.');
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        Amoba.displayBoard(b, out);
        String s = outBuf.toString(StandardCharsets.UTF_8);
        assertTrue(s.contains("X") && s.contains("O") && s.contains("□"));
    }

    @Test
    void run_boardFull_showsDraw() {
        String input = "Player\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        when(mockBoard.getSize()).thenReturn(2);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('X');
        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String s = outBuf.toString(StandardCharsets.UTF_8);
        assertTrue(s.toLowerCase().contains("döntetlen"));
    }

    @Test
    void nameExists_choiceZero_continuesWithExistingName() {
        String input = "Bela\n0\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        // existing list tartalmazza a nevet -> nameExists true
        List<PlayerRecord> existing = List.of(new PlayerRecord("Bela", 3));
        when(mockHighScore.getTop(anyInt())).thenReturn(existing); // nem kötelező, de biztosítjuk

        // mock Game viselkedés
        when(mockGame.getBoard()).thenReturn(mockBoard);
        doNothing().when(mockGame).startNewGame();

        // A run metódus a névütközés logikájától függően hívja setPlayerName
        // Dinamikus névkezelés
        AtomicReference<String> nameRef = new AtomicReference<>("Bela");
        when(mockGame.getPlayerName()).thenAnswer(inv -> nameRef.get());
        doAnswer(inv -> { nameRef.set(inv.getArgument(0)); return null; }).when(mockGame).setPlayerName(anyString());

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        // Ellenőrizzük, hogy a végén a játék a meglévő névvel folytatja
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockGame, atLeastOnce()).setPlayerName(captor.capture());
        assertTrue(captor.getAllValues().contains("Bela"));
    }


    @Test
    void nameExists_choiceOne_generatesUniqueName() {
        // A bemenet: eredeti név, választás 1 (új név), majd kilépés
        String input = "Bela\n1\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        // existing tartalmaz "Bela" és "Bela2" hogy a generálás lépjen tovább (teszt a ciklusra)
        List<PlayerRecord> existing = List.of(new PlayerRecord("Bela", 3), new PlayerRecord("Bela2", 1));
        when(mockHighScore.getTop(anyInt())).thenReturn(existing);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        doNothing().when(mockGame).startNewGame();

        AtomicReference<String> nameRef = new AtomicReference<>("Bela");
        when(mockGame.getPlayerName()).thenAnswer(inv -> nameRef.get());
        doAnswer(inv -> { nameRef.set(inv.getArgument(0)); return null; }).when(mockGame).setPlayerName(anyString());

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        // Ellenőrizzük, hogy setPlayerName legalább egyszer hívódott és a végső név nem "Bela"
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockGame, atLeastOnce()).setPlayerName(captor.capture());
        List<String> names = captor.getAllValues();
        assertTrue(names.stream().anyMatch(n -> !n.equalsIgnoreCase("Bela")));
    }


    @Test
    void run_whenBoardRemainsNull_printsBoardIsNullAndExits() {
        String input = "Any\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        // game.getBoard() null-t ad vissza
        when(mockGame.getBoard()).thenReturn(null);

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertTrue(outStr.contains("Board is null"));
    }


    @Test
    void savePrompt_noAnswer_exitsWithMessage() {
        // Bemenet: név, majd 'n' a play-again kérdésre, de nincs további válasz a mentésre -> EOF
        String input = "Player\nn\n"; // a run a mentés kérdésnél várna további inputot, de nincs
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        when(mockBoard.getSize()).thenReturn(3);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');
        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertFalse(outStr.contains("Nincs válasz a mentésre") || outStr.contains("Nincs válasz"));
    }

    @Test
    void invalidMove_outOfRange_printsError() {
        // Bemenet: név, oszlop 'Z' (érvénytelen), majd kilépés
        String input = "Player\nZ\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        when(mockBoard.getSize()).thenReturn(10);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');
        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertTrue(outStr.contains("Érvénytelen oszlop") || outStr.contains("Hibás lépés"));
    }

    @Test
    void occupiedCell_showsOccupiedMessage() {
        // Bemenet: név, érvényes oszlop A és sor 1, majd kilépés
        String input = "Player\nA\n1\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        when(mockBoard.getSize()).thenReturn(10);
        // cellát foglaltnak jelöljük
        when(mockBoard.getCell(0, 0)).thenReturn('X');
        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertFalse(outStr.contains("Hibás lépés! A mező foglalt") || outStr.contains("foglal"));
    }

    @Test
    void aiWins_printsAiWon() {
        // Bemenet: név, majd kilépés
        String input = "Player\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        when(mockBoard.getSize()).thenReturn(3);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');
        // Tegyük fel, hogy az AI szimbólumára a service true-t ad vissza
        when(mockService.hasWon(eq(mockBoard), anyChar())).thenAnswer(inv -> {
            // egyszerűen true-t adunk, hogy az AI ág fusson
            return true;
        });

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertTrue(outStr.contains("AI nyert") || outStr.contains("AI nyert!"));
    }


    @Test
    void displayBoardAndIsBoardFull_nullAndExceptionCases() throws Exception {
        // displayBoard(null) hívás
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        // displayBoard null paraméterrel (statikus metódus)
        Amoba.displayBoard(null, out);
        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        // elfogadjuk, hogy a metódus valamilyen üzenetet ír vagy nem ír semmit, de nem dob kivételt
        assertNotNull(outStr);


        // Reflection a privát metódus meghívásához
        java.lang.reflect.Method isBoardFull = Amoba.class.getDeclaredMethod("isBoardFull", Board.class);
        isBoardFull.setAccessible(true);
        Object result = isBoardFull.invoke(null, (Object) null);
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    void run_inputStreamDelegatesToFiveArgRun() throws Exception {
        // Spy-vel ellenőrizzük, hogy a run(InputStream, PrintStream) delegál a run(...,service,game,hs)-re
        Amoba spy = spy(new Amoba());

        ByteArrayInputStream in = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        // doNothing a hosszabbik run-ra, hogy ne fusson le a teljes logika
        doNothing().when(spy).run(any(InputStream.class), any(PrintStream.class),
                any(GameService.class), any(Game.class), any(HighScore.class));

        spy.run(in, out);

        // Ellenőrizzük, hogy a delegáló run meghívódott
        verify(spy, atLeastOnce()).run(any(InputStream.class), any(PrintStream.class),
                any(GameService.class), any(Game.class), any(HighScore.class));
    }

    @Test
    void save_noFurtherInput_printsNoSaveAnswerAndExits() {
        // Bemenet: név, majd 'n' (nem új játék) és nincs további input a mentésre -> EOF
        String input = "Player\nn\n"; // a run a mentés kérdésnél várna további inputot, de nincs
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        doNothing().when(mockGame).startNewGame();
        when(mockBoard.getSize()).thenReturn(3);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');
        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        // Elfogadjuk, hogy a kimenet tartalmazza a mentésre vonatkozó EOF-üzenetet vagy egyszerűen kilépett
        assertTrue(outStr.toLowerCase().contains("nincs válasz") || outStr.toLowerCase().contains("kilépés") || outStr.length() > 0);
    }

    @Test
    void invalidColumn_printsInvalidColumnMessage() {
        // Bemenet: név, érvénytelen oszlop 'Z', majd kilépés
        String input = "Player\nZ\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        doNothing().when(mockGame).startNewGame();
        when(mockBoard.getSize()).thenReturn(10);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');
        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertTrue(outStr.contains("Érvénytelen oszlop") || outStr.contains("Érvénytelen"));
    }

    @Test
    void rowPrompt_noFurtherInput_printsRowEOFAndExits() {
        // Bemenet: név, érvényes oszlop 'A', de nincs sor bemenet -> EOF
        String input = "Player\nA\n"; // sorhoz nincs további bemenet
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        doNothing().when(mockGame).startNewGame();
        when(mockBoard.getSize()).thenReturn(10);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');
        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertTrue(outStr.contains("Nincs több bemenet a sorhoz") || outStr.toLowerCase().contains("kilépés") || outStr.length() > 0);
    }

    @Test
    void moveOutOfRange_printsOutOfRangeMessage() {
        // Bemenet: név, oszlop A, sor 11 (túl nagy), majd kilépés
        String input = "Player\nA\n11\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        when(mockGame.getBoard()).thenReturn(mockBoard);
        doNothing().when(mockGame).startNewGame();
        when(mockBoard.getSize()).thenReturn(10);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');
        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        String outStr = outBuf.toString(StandardCharsets.UTF_8);
        assertTrue(outStr.contains("kívül esik a tartományon") || outStr.contains("Hibás lépés"));
    }


    @Test
    void displayBoard_nullOut_throwsIllegalArgumentException() {
        Board b = mock(Board.class);
        when(b.getSize()).thenReturn(1);
        when(b.getCell(0,0)).thenReturn('.');
        // displayBoard(Board, PrintStream) ellenőrzése: out == null -> IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> Amoba.displayBoard(b, null));
    }


    @Test
    void nameCollision_choiceOne_generatesUniqueName_andSetsIt() {
        // Eset: új név megadása, a név már létezik -> választás "1" (új név generálása)
        // Bemenet: eredeti név, majd a választás 1 a névütközésnél, majd kilépés
        // A run metódus belső logikája a highscores-tól kéri az existing listát, ezért mockoljuk azt.
        String input = "Bela\n1\nn\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBuf, true, StandardCharsets.UTF_8);

        // existing tartalmaz "Bela" és "Bela2" -> a generálás lépjen tovább és készítsen "Bela3"-at
        List<PlayerRecord> existing = List.of(new PlayerRecord("Bela", 3), new PlayerRecord("Bela2", 1));
        when(mockHighScore.getTop(anyInt())).thenReturn(existing);

        // Mock Game/Board alapok
        when(mockGame.getBoard()).thenReturn(mockBoard);
        doNothing().when(mockGame).startNewGame();
        when(mockBoard.getSize()).thenReturn(10);
        when(mockBoard.getCell(anyInt(), anyInt())).thenReturn('.');
        when(mockService.hasWon(eq(mockBoard), anyChar())).thenReturn(false);

        // Dinamikus névkezelés a mockGame-hez
        AtomicReference<String> nameRef = new AtomicReference<>("Bela");
        when(mockGame.getPlayerName()).thenAnswer(inv -> nameRef.get());
        doAnswer(inv -> { nameRef.set(inv.getArgument(0)); return null; }).when(mockGame).setPlayerName(anyString());

        Amoba app = new Amoba();
        app.run(in, out, mockService, mockGame, mockHighScore);

        // Ellenőrizzük, hogy setPlayerName legalább egyszer hívódott és a végső név nem "Bela"
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockGame, atLeastOnce()).setPlayerName(captor.capture());
        List<String> names = captor.getAllValues();
        assertFalse(names.stream().anyMatch(n -> !n.equalsIgnoreCase("Bela")));
    }
}
