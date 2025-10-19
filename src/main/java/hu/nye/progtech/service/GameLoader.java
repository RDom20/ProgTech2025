package hu.nye.progtech.service;

import hu.nye.progtech.domain.Board;

import java.io.*;

public class GameLoader {

    public Board loadGame(String saveFile) throws IOException {
        File file = new File(saveFile);

        // Ellenőrizzük, hogy a fájl létezik-e és nem üres
        if (!file.exists() || file.length() == 0) {
            throw new IOException("A fájl üres vagy nem létezik.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Board board = new Board(10); // Alapértelmezett 10x10-es tábla

            int row = 0;
            while ((line = reader.readLine()) != null && row < board.getSize()) {
                // Skip the header line
                if (line.startsWith("| Sor\\Oszlop")) {
                    continue;
                }

                String[] parts = line.split("\\|");
                for (int col = 1; col < parts.length; col++) {
                    if (col - 1 < board.getSize() && parts[col].trim().length() > 0) {
                        char cell = parts[col].trim().charAt(0);
                        board.setCell(row, col - 1, cell);  // Board cell set
                    }
                }
                row++;
            }

            return board;
        } catch (IOException e) {
            throw new IOException("Hiba történt a játék betöltésekor.", e);
        }
    }
}
