package hu.nye.progtech;

import hu.nye.progtech.domain.Board;
import hu.nye.progtech.domain.Coordinate;
import hu.nye.progtech.domain.Player;
import hu.nye.progtech.service.Game;
import hu.nye.progtech.service.GameService;

import java.io.*;
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

        // Játékos létrehozása
        System.out.print("Add meg a játékos nevét: ");
        String playerName = scanner.nextLine();
        Player player = new Player(playerName, 'X');  // Játékos 'X' szimbólummal

        // AI létrehozása
        Player ai = new Player("AI", 'O');  // AI 'O' szimbólummal

        // Játék tábla létrehozása
        Board board = new Board(MAP_SIZE);  // 10x10-es tábla

        Game game = new Game();  // Game objektum létrehozása
        game.startGame();  // A játék indítása

        boolean gameOver = false;
        boolean isPlayerTurn = true;  // A játékos kezd
        boolean isAIFirstMove = true;  // Egy változó, hogy kövessük, hogy az AI lépett-e már

        while (!gameOver) {
            displayBoard(board);

            if (isPlayerTurn) {
                // Játékos lépése
                System.out.println("Játékos: " + player.getName() + " (" + player.getSymbol() + ")");
                String columnInput;
                int row = -1, col = -1;
                boolean validInput = false;

                while (!validInput) {
                    System.out.print("Oszlop (A-J): ");
                    columnInput = scanner.nextLine().toUpperCase();

                    if (columnInput.matches("[A-J]")) {
                        col = columnInput.charAt(0) - 'A';
                    } else if (columnInput.equalsIgnoreCase("esc")) {
                        System.out.print("Szeretnéd menteni a játékot? (Igen/Nem): ");
                        String saveAnswer = scanner.nextLine();
                        List<String> acceptedSaveAnswers = Arrays.asList("igen", "i");

                        if (acceptedSaveAnswers.contains(saveAnswer)) {
                            saveGame(board, player, ai);  // Játék mentése
                            System.out.println("Játék mentve!");
                        } else {
                            System.out.println("A játék nem lett mentve.");
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
                        board.placeSymbol(row, col, player.getSymbol());
                        validInput = true;
                    } else {
                        System.out.println("Hibás lépés! Kérlek válassz egy üres mezőt.");
                    }
                }

                if (service.hasWon(board, player.getSymbol())) {
                    displayBoard(board);
                    System.out.println(player.getName() + " nyert!");
                    break;
                }

                isPlayerTurn = false;  // A következő lépés az AI-é
            } else {
                // AI lépése
                Coordinate aiMove = service.placeNextAIMove(board, ai);

                if (aiMove != null) {
                    board.placeSymbol(aiMove.getRow(), aiMove.getCol(), ai.getSymbol());
                    // AI lépésének kiírása
                    System.out.println("AI köre: AI (" + ai.getSymbol() + ")");
                    System.out.println("AI lépése: Oszlop " + (char) ('A' + aiMove.getCol()) + ", Sor " + (aiMove.getRow() + 1));
                }

                if (service.hasWon(board, ai.getSymbol())) {
                    displayBoard(board);
                    System.out.println(ai.getName() + " nyert!");
                    break;
                }

                isPlayerTurn = true;  // A következő lépés a játékosé
            }

            // Ellenőrizzük, hogy a tábla megtelt-e döntetlennel
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
                if (board.getCell(row, col) == '.') {
                    return false;
                }
            }
        }
        return true;
    }

    public static void displayBoard(Board board) {
        String RESET = "\033[0m";      // Visszaállítja az alap színt
        String BLUE = "\033[34m";      // Kék szín (X-hez)
        String RED = "\033[31m";       // Piros szín (O-hoz)
        String EMPTY = "\033[37m";     // Fehér szín (üres cella)

        // Fejléc kiírása (A, B, C, ... oszlopok)
        System.out.print("   ");  // A fejléchez megfelelő eltolás
        for (char c = 'A'; c < 'A' + MAP_SIZE; c++) {
            System.out.printf("%-3c", c);  // Oszlopok (A-J) balra igazítva
        }
        System.out.println();

        // Táblázat megjelenítése
        for (int i = 0; i < MAP_SIZE; i++) {
            System.out.printf("%-3d", i + 1);  // Sorok (1-10) balra igazítva
            for (int j = 0; j < MAP_SIZE; j++) {
                char cell = board.getCell(i, j);
                if (cell == 'X') {
                    System.out.printf(BLUE + "%-3s" + RESET, "X");  // X kék
                } else if (cell == 'O') {
                    System.out.printf(RED + "%-3s" + RESET, "O");   // O piros
                } else {
                    System.out.printf(EMPTY + "%-3s" + RESET, "\u25A1");  // Üres cella (□)
                }
            }
            System.out.println();  // Sor vége
        }
    }

    // Játék mentése fájlba
    private static void saveGame(Board board, Player player, Player ai) {
        File saveFile = new File("game_save.md");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            // Játékos adatok írása
            writer.write("Játékos: " + player.getName() + " (" + player.getSymbol() + ")\n");
            writer.write("AI: " + ai.getName() + " (" + ai.getSymbol() + ")\n");

            // Tábla mentése
            writer.write("Tábla:\n");
            // Fejléc kiírása (A, B, C, ... oszlopok)
            writer.write("\n    ");  // A fejléchez megfelelő eltolás
            for (char c = 'A'; c < 'A' + board.getSize(); c++) {
                writer.write(String.format("%-3c", c));  // Oszlopok (A-J) balra igazítva
            }
            writer.write("\n");

            // Táblázat megjelenítése
            for (int i = 0; i < board.getSize(); i++) {
                writer.write(String.format("%-3d", i + 1));  // Sorok (1-10) balra igazítva
                for (int j = 0; j < board.getSize(); j++) {
                    char cell = board.getCell(i, j);
                    if (cell == 'X') {
                        writer.write(" X ");  // X
                    } else if (cell == 'O') {
                        writer.write(" O ");  // O
                    } else {
                        writer.write(" □ ");  // Üres cella (□)
                    }
                }
                writer.write("\n");  // Sor vége
            }
        } catch (IOException e) {
            System.out.println("Hiba történt a játék mentésekor: " + e.getMessage());
        }
    }
}
