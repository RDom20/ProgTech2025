package hu.nye.progtech;

import hu.nye.progtech.domain.Board;
import hu.nye.progtech.domain.Player;
import hu.nye.progtech.service.Game;
import hu.nye.progtech.service.GameService;
import hu.nye.progtech.service.HighScoreService;
import hu.nye.progtech.persistence.DatabaseManager;
import hu.nye.progtech.persistence.PlayerDao;
import hu.nye.progtech.persistence.PlayerRecord;


import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Amoba {

    private static final int MAP_SIZE = 10;

    public static void main(String[] args) {
        System.out.println("=== Amőba játék ===");
        System.out.println("Készítette: Ruska Dominik (FD69KA)");

        Scanner scanner = new Scanner(System.in);
        GameService service = new GameService();

        // A Game osztályban bekért név használata
        DatabaseManager db = new DatabaseManager();
        PlayerDao dao = new PlayerDao(db);
        HighScoreService hs = new HighScoreService(dao);
        Game game = new Game();
        game.startGame(); // betöltés vagy új játék

        String playerName = game.getPlayerName();
        Player player = new Player(playerName, 'X');
        Player ai = new Player("AI", 'O');

        Board board = game.getBoard();
        boolean gameOver = false;
        boolean isPlayerTurn = board.getCurrentPlayer() == 'X';

        while (!gameOver) {
            displayBoard(board);

            if (isPlayerTurn) {
                System.out.println("Játékos: " + player.getName() + " (" + player.getSymbol() + ")");
                int row = -1, col = -1;
                boolean validInput = false;

                while (!validInput) {
                    System.out.print("Oszlop (A-J): ");
                    String columnInput = scanner.nextLine().toUpperCase();

                    if (columnInput.matches("[A-J]")) {
                        col = columnInput.charAt(0) - 'A';
                    } else if (columnInput.equalsIgnoreCase("esc")) {
                        System.out.print("Szeretnéd menteni a játékot? (Igen/Nem): ");
                        String saveAnswer = scanner.nextLine();
                        List<String> acceptedSaveAnswers = Arrays.asList("igen", "i");
                        if (acceptedSaveAnswers.contains(saveAnswer.toLowerCase())) {
                            // Generáljuk a mentés fájlnevét és kérjük a Game.saveGame-et, ami belül kezeli az útvonalat
                            String fileName = game.generateNewSaveFileName();
                            game.saveGame(fileName);
                            System.out.println("Játék mentve!");
                        }
                        System.out.println("Kilépés...");
                        return;
                    }


                    System.out.print("Sor (1-10): ");
                    String rowInput = scanner.nextLine();
                    try {
                        row = Integer.parseInt(rowInput) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Hibás bemenet!");
                        continue;
                    }

                    if (row >= 0 && row < MAP_SIZE && col >= 0 && col < MAP_SIZE && board.getCell(row, col) == '.') {
                        game.makeMove(row, col);
                        validInput = true;
                    } else {
                        System.out.println("Hibás lépés! Válassz egy üres mezőt.");
                    }
                }

                if (service.hasWon(board, player.getSymbol())) {
                    displayBoard(board);
                    System.out.println(player.getName() + " nyert!");
                    // Highscore frissítése
                    hs.recordWin(player.getName());

                    // Highscore megjelenítése a győzelem után
                    HighScoreService.displayHighScores(hs, 10);

                    break;
                }

                isPlayerTurn = false;

            } else {
                // AI lépés
                hu.nye.progtech.domain.Coordinate aiMove = service.placeNextAIMove(board, ai);
                if (aiMove != null) {
                    game.makeMove(aiMove.getRow(), aiMove.getCol());
                    System.out.println("AI lépése: " + (char)('A' + aiMove.getCol()) + (aiMove.getRow() + 1));
                }

                if (service.hasWon(board, ai.getSymbol())) {
                    displayBoard(board);
                    System.out.println(ai.getName() + " nyert!");
                    hs.recordWin(ai.getName());      // vagy csak emberi játékosoknál hívjátok
                    HighScoreService.displayHighScores(hs, 10);

                    break;
                }

                isPlayerTurn = true;
            }

            if (isBoardFull(board)) {
                displayBoard(board);
                System.out.println("Döntetlen!");
                break;
            }
        }

        scanner.close();
    }

    private static boolean isBoardFull(Board board) {
        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                if (board.getCell(row, col) == '.') return false;
            }
        }
        return true;
    }

    public static void displayBoard(Board board) {
        String RESET = "\033[0m";
        String BLUE = "\033[34m";
        String RED = "\033[31m";
        String EMPTY = "\033[37m";

        System.out.print("   ");
        for (char c = 'A'; c < 'A' + MAP_SIZE; c++) System.out.printf("%-3c", c);
        System.out.println();

        for (int i = 0; i < MAP_SIZE; i++) {
            System.out.printf("%-3d", i + 1);
            for (int j = 0; j < MAP_SIZE; j++) {
                char cell = board.getCell(i, j);
                if (cell == 'X') System.out.printf(BLUE + "%-3s" + RESET, "X");
                else if (cell == 'O') System.out.printf(RED + "%-3s" + RESET, "O");
                else System.out.printf(EMPTY + "%-3s" + RESET, "\u25A1");
            }
            System.out.println();
        }
    }
}
