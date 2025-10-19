package hu.nye.progtech.service;

import hu.nye.progtech.domain.Board;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GameSaver {

    public void saveGame(Board board, String fileName, char currentPlayer) throws IOException {
        // Create the file if it doesn't exist, else overwrite
        File saveFile = new File(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
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
}
