package hu.nye.progtech;

import hu.nye.progtech.domain.Board;
import hu.nye.progtech.domain.Player;
import hu.nye.progtech.persistence.DatabaseManager;
import hu.nye.progtech.persistence.PlayerDao;
import hu.nye.progtech.service.Game;
import hu.nye.progtech.service.GameService;
import hu.nye.progtech.service.HighScore;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Amoba alkalmazás.
 * - A main egyszerű bootstrap: delegál a tesztelhető run(...) metódusra.
 * - A run(...) metódus injektálható komponenseket fogad, így unit tesztekben mockolható.
 *
 * A fájl tartalmazza az eredeti interaktív logikát (mentés/ betöltés menüvel),
 * ugyanakkor van egy run(InputStream, PrintStream, GameService, Game, HighScore) overload,
 * amit a tesztek meghívhatnak ugyanabban a JVM-ben (JaCoCo lefedettséghez).
 */
public class Amoba {

    private static final int MAP_SIZE = 10;

    public static void main(String[] args) {
        // bootstrap: delegálunk a run metódusra az alapértelmezett komponensekkel
        new Amoba().run(System.in, System.out);
    }

    /**
     * Tesztbarát run metódus: alapértelmezett komponensekkel.
     */
    public void run(InputStream in, PrintStream out) {
        Objects.requireNonNull(in);
        Objects.requireNonNull(out);

        GameService service = new GameService();
        DatabaseManager db = new DatabaseManager();
        PlayerDao dao = new PlayerDao(db);
        HighScore hs = new HighScore(dao);
        Game game = new Game();

        run(in, out, service, game, hs);
    }

    /**
     * Tesztelhető run metódus, amely injektálható GameService, Game és HighScore példányokat fogad.
     * A tesztek ezt a metódust hívják, így a logika ugyanabban a JVM-ben fut és JaCoCo lefedi.
     */
    public void run(InputStream in, PrintStream out,
                    GameService service, Game game, HighScore hs) {

        Objects.requireNonNull(in);
        Objects.requireNonNull(out);
        Objects.requireNonNull(service);
        Objects.requireNonNull(game);
        Objects.requireNonNull(hs);

        out.println("=== Amőba játék ===");
        out.println("Készítette: Ruska Dominik (FD69KA)");

        Scanner scanner = new Scanner(in);

        // Eredeti interaktív startGame logika (betöltés / új játék)
        game.startGame();

        String playerName = game.getPlayerName();
        if (playerName == null || playerName.isBlank()) {
            playerName = "Player";
        }
        Player player = new Player(playerName, 'X');
        Player ai = new Player("AI", 'O');

        Board board = game.getBoard();
        if (board == null) {
            out.println("Board is null");
            return;
        }

        boolean gameOver = false;
        boolean isPlayerTurn = board.getCurrentPlayer() == 'X';

        while (!gameOver) {
            displayBoard(board, out);

            if (isPlayerTurn) {
                out.println("Játékos: " + player.getName() + " (" + player.getSymbol() + ")");
                int row = -1;
                int col = -1;
                boolean validInput = false;

                while (!validInput) {
                    out.print("Oszlop (A-J): ");
                    if (!scanner.hasNextLine()) {
                        out.println("Nincs több bemenet, kilépés.");
                        return;
                    }
                    String columnInput = scanner.nextLine().trim().toUpperCase();

                    if (columnInput.matches("[A-J]")) {
                        col = columnInput.charAt(0) - 'A';
                    } else if ("ESC".equalsIgnoreCase(columnInput)) {
                        out.print("Szeretnéd menteni a játékot? (Igen/Nem): ");
                        if (!scanner.hasNextLine()) {
                            out.println("Nincs válasz a mentésre, kilépés.");
                            return;
                        }
                        String saveAnswer = scanner.nextLine().trim();
                        List<String> acceptedSaveAnswers = List.of("igen", "i");
                        if (acceptedSaveAnswers.contains(saveAnswer.toLowerCase())) {
                            String fileName = game.generateNewSaveFileName();
                            game.saveGame(fileName);
                            out.println("Játék mentve!");
                        }
                        out.println("Kilépés...");
                        return;
                    } else {
                        out.println("Érvénytelen oszlop. Adj meg egy betűt A és J között.");
                        continue;
                    }

                    out.print("Sor (1-10): ");
                    if (!scanner.hasNextLine()) {
                        out.println("Nincs több bemenet a sorhoz, kilépés.");
                        return;
                    }
                    String rowInput = scanner.nextLine().trim();
                    try {
                        row = Integer.parseInt(rowInput) - 1;
                    } catch (NumberFormatException e) {
                        out.println("Hibás bemenet! Adj meg egy számot 1 és 10 között.");
                        continue;
                    }

                    if (row >= 0 && row < MAP_SIZE && col >= 0 && col < MAP_SIZE) {
                        if (board.getCell(row, col) == '.') {
                            // Biztosítsuk, hogy a makeMove a megfelelő játékoshoz tartozó szimbólumot használja.
                            // Ha a Game.makeMove implicit currentPlayer-t használ, akkor a Game/Board implementációnak kell
                            // gondoskodnia a currentPlayer frissítéséről. Itt a run logikája hívja meg a makeMove-ot.
                            game.makeMove(row, col);
                            validInput = true;
                        } else {
                            out.println("Hibás lépés! A mező foglalt. Válassz egy üres mezőt.");
                        }
                    } else {
                        out.println("Hibás lépés! Sor vagy oszlop kívül esik a tartományon.");
                    }
                }

                if (service.hasWon(board, player.getSymbol())) {
                    displayBoard(board, out);
                    out.println(player.getName() + " nyert!");
                    hs.recordWin(player.getName());
                    HighScore.displayHighScores(hs, 10);
                    break;
                }

                isPlayerTurn = false;

            } else {
                // AI lépés
                hu.nye.progtech.domain.Coordinate aiMove = service.placeNextAIMove(board, ai);
                if (aiMove != null) {
                    game.makeMove(aiMove.getRow(), aiMove.getCol());
                    out.println("AI lépése: " + (char) ('A' + aiMove.getCol()) + (aiMove.getRow() + 1));
                }

                if (service.hasWon(board, ai.getSymbol())) {
                    displayBoard(board, out);
                    out.println(ai.getName() + " nyert!");
                    hs.recordWin(ai.getName());
                    HighScore.displayHighScores(hs, 10);
                    break;
                }

                isPlayerTurn = true;
            }

            if (isBoardFull(board)) {
                displayBoard(board, out);
                out.println("Döntetlen!");
                break;
            }
        }

        scanner.close();
    }

    /**
     * Tesztbarát displayBoard, PrintStream paraméterrel.
     */
    public static void displayBoard(Board board, PrintStream out) {
        final String RESET = "\033[0m";
        final String BLUE = "\033[34m";
        final String RED = "\033[31m";
        final String GRAY = "\033[37m";

        if (out == null) {
            throw new IllegalArgumentException("PrintStream must not be null");
        }
        if (board == null) {
            out.println("Board is null");
            return;
        }

        final int size = board.getSize();

        out.print("   ");
        for (int i = 0; i < MAP_SIZE; i++) {
            out.printf("%-3c", (char) ('A' + i));
        }
        out.println();

        for (int r = 0; r < size; r++) {
            out.printf("%-3d", r + 1);
            for (int c = 0; c < size; c++) {
                char ch;
                try {
                    ch = board.getCell(r, c);
                } catch (IndexOutOfBoundsException ex) {
                    ch = '.';
                }

                if (ch == 'X') {
                    out.printf(BLUE + "%-3s" + RESET, "X");
                } else if (ch == 'O') {
                    out.printf(RED + "%-3s" + RESET, "O");
                } else {
                    out.printf(GRAY + "%-3s" + RESET, "□");
                }
            }
            out.println();
        }
    }

    /**
     * Régi kompatibilitás: ha nincs PrintStream megadva, System.out-ot használjuk.
     */
    public static void displayBoard(Board board) {
        displayBoard(board, System.out);
    }

    /**
     * Privát segéd, a tesztek reflection-nel hívhatják.
     */
    private static boolean isBoardFull(Board board) {
        if (board == null) return false;
        final int size = board.getSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                try {
                    if (board.getCell(r, c) == '.') {
                        return false;
                    }
                } catch (IndexOutOfBoundsException ex) {
                    return false;
                }
            }
        }
        return true;
    }
}
