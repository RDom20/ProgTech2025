package hu.nye.progtech.service;

import hu.nye.progtech.domain.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private Board board;
    private char currentPlayerSymbol;
    private List<String> playerMoves;
    private List<String> aiMoves;
    private String playerName;
    private static final String SAVE_DIR = "D:\\infó\\j\\ProgTech2025";  // Állítsd be a megfelelő mentési könyvtárat

    public Game() {
        currentPlayerSymbol = 'X';
        playerMoves = new ArrayList<>();
        aiMoves = new ArrayList<>();
    }

    private File getSaveDir() {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public String getSaveFilePath(String fileName) {
        return SAVE_DIR + File.separator + fileName;
    }

    public String generateNewSaveFileName() {
        int index = 1;
        String fileName;

        do {
            fileName = "saved_game_" + playerName + index + ".xml";
            index++;
        } while (new File(getSaveFilePath(fileName)).exists());

        return fileName;
    }

    // ============================ JÁTÉK INDÍTÁSA ============================
    public void startGame() {
        Scanner scanner = new Scanner(System.in);

        playerNameLoop:
        while (true) {

            System.out.print("Add meg a játékos nevét: ");
            playerName = scanner.nextLine().trim();

            File[] userFiles = getSaveDir().listFiles(
                    (d, name) -> name.startsWith("saved_game_" + playerName)
            );

            // Ha van mentés
            if (userFiles != null && userFiles.length > 0) {

                System.out.println("Találtunk egy mentett játékot:");
                System.out.println("[0] Új játék");
                for (int i = 0; i < userFiles.length; i++) {
                    System.out.printf("[%d] %s\n", i + 1, userFiles[i].getName());
                }

                // Mentés kiválasztása
                saveSelectionLoop:
                while (true) {

                    System.out.print("Válasszon mentést!: (szám, n kilépés, e visszalépés) ");
                    String input = scanner.nextLine().trim();

                    if (input.equalsIgnoreCase("n")) {
                        System.out.println("Kilépés...");
                        System.exit(0);
                    }

                    if (input.equalsIgnoreCase("e")) {
                        continue playerNameLoop;
                    }

                    int choice;
                    try {
                        choice = Integer.parseInt(input);
                    } catch (Exception ex) {
                        System.out.println("Érvénytelen választás!");
                        continue saveSelectionLoop;
                    }

                    if (choice == 0) {
                        startNewGame();
                        break playerNameLoop;
                    }

                    if (choice >= 1 && choice <= userFiles.length) {

                        File selectedFile = userFiles[choice - 1];

                        System.out.print("Mit szeretne csinálni? (load/0, del/1, n, e): ");
                        String action = scanner.nextLine().trim().toLowerCase();

                        if (action.equals("0") || action.startsWith("load")) {
                            try {
                                loadGame(selectedFile.getAbsolutePath());
                                break playerNameLoop;
                            } catch (IOException e) {
                                System.out.println("Hiba történt a betöltéskor!");
                                startNewGame();
                                break playerNameLoop;
                            }
                        }

                        if (action.equals("1") || action.startsWith("del")) {
                            if (selectedFile.delete()) {
                                System.out.println("Mentés törölve.");
                            } else {
                                System.out.println("Nem sikerült törölni.");
                            }
                            continue saveSelectionLoop;
                        }

                        if (action.equals("n")) {
                            System.out.println("Kilépés...");
                            System.exit(0);
                        }

                        if (action.equals("e")) {
                            continue saveSelectionLoop;
                        }

                        System.out.println("Érvénytelen választás!");
                    } else {
                        System.out.println("Érvénytelen szám!");
                    }
                }

            } else {
                startNewGame();
                break;
            }
        }
    }

    // ============================ ÚJ JÁTÉK ============================
    public void startNewGame() {
        if (playerName == null || playerName.isEmpty()) {
            playerName = "Játékos"; // alapértelmezett név
        }
        board = new Board(10);
        currentPlayerSymbol = 'X';
        playerMoves = new ArrayList<>();
        aiMoves = new ArrayList<>();
        System.out.println("Új játék kezdődött a játékos számára: " + playerName);
    }

    // ============================ LÉPÉS ============================
    public void makeMove(int row, int col) {
        board.setCell(row, col, currentPlayerSymbol);

        String coordinate = String.format("%c%d", (char) ('A' + col), row + 1);

        if (currentPlayerSymbol == 'X') {
            playerMoves.add(coordinate);
        } else {
            aiMoves.add(coordinate);
        }

        currentPlayerSymbol = (currentPlayerSymbol == 'X') ? 'O' : 'X';
        board.setCurrentPlayer(currentPlayerSymbol);
    }

    // ============================ BETÖLTÉS ============================
    public void loadGame(String saveFilePath) throws IOException {
        File file = new File(saveFilePath);
        if (!file.exists()) throw new FileNotFoundException("A mentés nem található!");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            board = new Board(10);

            // playerName betöltése
            String line = reader.readLine();
            if (line != null && line.contains(":")) {
                playerName = line.split(":")[1].trim();
            }

            // Aktuális játékos betöltése
            line = reader.readLine();
            if (line != null && line.contains(":")) {
                String symbol = line.split(":")[1].trim();  // Ez a sor a szimbólumot olvassa be
                if (symbol.length() > 0) {
                    currentPlayerSymbol = symbol.charAt(0);
                }

                // Ha a karakter nem X vagy O, akkor hiba
                if (currentPlayerSymbol != 'X' && currentPlayerSymbol != 'O') {
                    System.out.println("Hiba: Hibás játékos szimbólum a mentett fájlban: " + currentPlayerSymbol);
                    currentPlayerSymbol = 'X';  // Alapértelmezett
                }
            } else {
                currentPlayerSymbol = 'X';  // alapértelmezett
            }
            board.setCurrentPlayer(currentPlayerSymbol);

            // X lépések betöltése
            playerMoves.clear();
            line = reader.readLine();
            if (line != null && line.contains(":")) {
                for (String move : line.split(":")[1].trim().split(", ")) {
                    if (!move.isEmpty()) playerMoves.add(move);
                }
            }

            // O lépések betöltése
            aiMoves.clear();
            line = reader.readLine();
            if (line != null && line.contains(":")) {
                for (String move : line.split(":")[1].trim().split(", ")) {
                    if (!move.isEmpty()) aiMoves.add(move);
                }
            }

            // üres sor átugrása
            reader.readLine();

            // pálya beolvasása
            for (int r = 0; r < board.getSize(); r++) {
                line = reader.readLine();
                if (line == null) break;

                // Az egy vagy több szóköz mentén splitelünk
                String[] tokens = line.trim().split(" +");

                // Ha az első token szám (sorszám), akkor azt kihagyjuk, offset = 1
                int offset = 0;
                if (tokens.length > 0) {
                    String first = tokens[0];
                    if (first.matches("\\d+")) offset = 1;
                }

                for (int c = 0; c < board.getSize(); c++) {
                    int tokenIndex = c + offset;
                    if (tokenIndex >= tokens.length) break;
                    char ch = tokens[tokenIndex].charAt(0);

                    // Ha a mentésben üres jelet használsz (□), azt '.'-ra cseréljük
                    if (ch == '□' || ch == '\u25A1') {
                        board.setCell(r, c, '.');
                    } else {
                        board.setCell(r, c, ch);
                    }
                }
            }


            System.out.println("Játék betöltve: " + playerName);
        }
    }

    // ============================ MENTÉS ============================
    public void saveGame() {
        saveGame(generateNewSaveFileName());
    }

    // Ha itt nem történik I/O művelet, a kivétel kezelése felesleges.
    public void saveGame(String fileName) {
        try {
            // Ha nem történik I/O művelet, az IOException-nek nincs értelme.
            BufferedWriter writer = new BufferedWriter(new FileWriter(getSaveFilePath(fileName)));
            writer.write("Felhasználó: " + playerName + "\n");
            writer.write("Aktuális játékos: " + currentPlayerSymbol + "\n");
            writer.write("X játékos lépései: " + String.join(", ", playerMoves) + "\n");
            writer.write("O játékos lépései: " + String.join(", ", aiMoves) + "\n");
            writer.write("\n");

            writer.write("   ");
            for (char c = 'A'; c < 'A' + board.getSize(); c++) {
                writer.write(c + "  ");
            }
            writer.write("\n");

            for (int r = 0; r < 10; r++) {
                writer.write(String.format("%-3d", r + 1));
                for (int c = 0; c < 10; c++) {
                    char cell = board.getCell(r, c);
                    if (cell == '.' || cell == 0) writer.write("□  ");
                    else writer.write(cell + "  ");
                }
                writer.write("\n");
            }

            System.out.println("A játék elmentve: " + fileName);

            writer.close();  // Ne felejtsd el bezárni a writer-t
        } catch (IOException e) {
            System.out.println("Hiba történt a mentésnél: " + e.getMessage());
        }
    }


    // ============================ GETTEREK ============================
    public Board getBoard() {
        return board;
    }

    public char getCurrentPlayerSymbol() {
        return currentPlayerSymbol;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
