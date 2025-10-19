package hu.nye.progtech.service;

import hu.nye.progtech.domain.Board;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;


public class Game {
    private static final String SAVE_FILE = "D:\\infó\\j\\Amoba\\game_save.md";  // A mentett játék fájl neve
    private Board board;
    private GameLoader gameLoader;
    private GameSaver gameSaver;
    private Scanner scanner = new Scanner(System.in);
    private String currentPlayer; // Aktuális játékos (X vagy O)

    private List<String> playerMoves;  // Az X játékos lépései (koordináták)
    private List<String> aiMoves;      // Az O játékos lépései (koordináták)

    public Game() {
        gameLoader = new GameLoader();
        gameSaver = new GameSaver();
        currentPlayer = "X";  // Kezdetben az X játékos kezd
        playerMoves = new ArrayList<>();
        aiMoves = new ArrayList<>();
    }

    public void startGame() {
        // Ellenőrizzük, hogy létezik-e mentett játék
        File saveFile = new File(SAVE_FILE);
        if (saveFile.exists() && saveFile.length() > 0) {  // Ha a fájl nem üres
            System.out.println("Találtunk egy mentett játékot. Szeretnéd folytatni? (Igen/Nem)");
            String answer = scanner.nextLine().toLowerCase();  // Kisebb-nagyobb betűk nem számítanak

            List<String> acceptedAnswers = Arrays.asList("igen", "i");

            if (acceptedAnswers.contains(answer)) {
                try {
                    loadGame(SAVE_FILE);  // Betöltjük a mentett játékot
                } catch (IOException e) {
                    System.out.println("Hiba történt a játék betöltésekor: " + e.getMessage());
                }
            } else {
                startNewGame(); // Új játék indítása
            }

        } else {
            System.out.println("Nincs mentett játék, vagy a fájl üres. Új játékot indítunk.");
            startNewGame();  // Új játék indítása
        }
    }

    public void startNewGame() {
        board = new Board(10);  // Alapértelmezett 10x10-es tábla
        currentPlayer = "X";  // Kezdetben az X játékos kezd
        System.out.println("Új játék kezdődött!");

        // Létrehozzuk a mentett fájlt az alapértelmezett állapotban
        createSaveFile();  // Csak akkor szükséges, ha új fájlra van szükség

        // Az aktuális állapot mentése
        saveGame();  // Hívjuk meg a saveGame-t, hogy az új játékot mentsük el
    }


    public void loadGame(String saveFilePath) throws IOException {
        // Ellenőrizzük, hogy létezik-e a fájl
        File file = new File(saveFilePath);
        if (!file.exists()) {
            throw new FileNotFoundException("A fájl nem található: " + saveFilePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int row = 0;

            // Ha a board null, akkor inicializáljuk
            if (board == null) {
                board = new Board(10);  // Például alapértelmezett 10x10-es tábla
            }

            // Beolvassuk a tábla sorait
            while ((line = reader.readLine()) != null && row < board.getSize()) {
                // A sorokat csak akkor dolgozzuk fel, ha nem üres
                if (!line.trim().isEmpty()) {
                    String[] cells = line.trim().split("\\s+");
                    for (int col = 0; col < cells.length && col < board.getSize(); col++) {
                        // A cellák X, O vagy üres (□) lehetnek
                        if ("X".equals(cells[col])) {
                            board.setCell(row, col, 'X');
                        } else if ("O".equals(cells[col])) {
                            board.setCell(row, col, 'O');
                        } else {
                            board.setCell(row, col, '.');  // Üres mező
                        }
                    }
                    row++;
                }
            }

            // Beolvassuk az aktuális játékost
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Aktuális játékos:")) {
                    currentPlayer = line.split(":")[1].trim();
                } else if (line.startsWith("X játékos lépései:")) {
                    // Az X játékos lépéseinek betöltése
                    String movesLine = line.split(":")[1].trim();
                    String[] moves = movesLine.split(", ");
                    for (String move : moves) {
                        playerMoves.add(move);  // Az X lépései
                    }
                } else if (line.startsWith("O játékos lépései:")) {
                    // Az O játékos lépéseinek betöltése
                    String movesLine = line.split(":")[1].trim();
                    String[] moves = movesLine.split(", ");
                    for (String move : moves) {
                        aiMoves.add(move);  // Az O lépései
                    }
                }
            }

            System.out.println("Játék betöltve!");
        } catch (IOException e) {
            System.out.println("Hiba történt a játék betöltésekor: " + e.getMessage());
        }
    }
    public void saveGame() {
        File saveDir = new File("D:\\infó\\j\\Amoba");

        // Az 'O' játékos lépéseinek kijavítása, ha még üres cella
        for (int i = 0; i < aiMoves.size(); i++) {
            if (aiMoves.get(i).equals(" □ ")) {
                aiMoves.set(i, "O");  // Cseréljük 'O'-ra
            }
        }

// Az 'X' játékos lépéseinek kijavítása, ha még üres cella
        for (int i = 0; i < playerMoves.size(); i++) {
            if (playerMoves.get(i).equals(" □ ")) {
                playerMoves.set(i, "X");  // Cseréljük 'X'-re
            }
        }

        // Ellenőrizzük, hogy a mappa létezik-e, ha nem, létrehozzuk
        if (!saveDir.exists()) {
            if (saveDir.mkdirs()) {
                System.out.println("A mappa létrehozva: " + saveDir.getPath());
            } else {
                System.out.println("Hiba történt a mappa létrehozásakor.");
                return;
            }
        }

        // A fájl elérési útja
        File file = new File(saveDir, "game_save.md");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Fejléc kiírása (A, B, C, ... oszlopok)
            writer.write("    ");  // A fejléchez megfelelő eltolás
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

            // Játékos információk mentése
            writer.write("\nAktuális játékos: " + currentPlayer + "\n");

            // Az X és O játékosok lépéseinek mentése
            writer.write("\nX játékos lépései: " + String.join(", ", playerMoves) + "\n");
            writer.write("O játékos lépései: " + String.join(", ", aiMoves) + "\n");

            System.out.println("Játék mentve!");
        } catch (IOException e) {
            System.out.println("Hiba történt a játék mentésekor: " + e.getMessage());
        }
    }

    public void createSaveFile() {
        // Fájl létrehozása és formázott táblázat írása
        File saveDir = new File("D:\\infó\\j\\Amoba");

        // Ellenőrizzük, hogy a mappa létezik-e, ha nem, létrehozzuk
        if (!saveDir.exists()) {
            if (saveDir.mkdirs()) {
                System.out.println("A mappa létrehozva: " + saveDir.getPath());
            } else {
                System.out.println("Hiba történt a mappa létrehozásakor.");
                return;
            }
        }

        File file = new File(saveDir, "game_save.md");


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Fejléc kiírása (A, B, C, ... oszlopok)
            writer.write("    ");  // A fejléchez megfelelő eltolás
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

            // Játékos információk mentése
            writer.write("\nAktuális játékos: " + currentPlayer + "\n");

            System.out.println("Mentett fájl létrehozva!");
        } catch (IOException e) {
            System.out.println("Hiba történt a fájl létrehozása közben: " + e.getMessage());
        }
    }

    // A játékosok lépéseinek hozzáadása
    public void makeMove(int row, int col) {
        char currentChar = (currentPlayer.equals("X")) ? 'X' : 'O';
        board.setCell(row, col, currentChar);

        // Koordináták formázása (pl. 'a5', 'c8')
        String coordinate = String.format("%c%d", (char) ('A' + col), row + 1);

        if (currentPlayer.equals("X")) {
            playerMoves.add(coordinate);  // Az X játékos lépése
        } else {
            aiMoves.add(coordinate);      // Az O játékos lépése
        }

        // Váltsunk játékost
        currentPlayer = (currentPlayer.equals("X")) ? "O" : "X";
    }


}
