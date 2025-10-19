package hu.nye.progtech.service;

import hu.nye.progtech.domain.Board;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GameLoader {

    public Board loadGame(String fileName) throws IOException {
        Board board = new Board(10); // Alapértelmezett 10x10-es tábla
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < board.getSize()) {
                String[] cells = line.split(" ");
                for (int col = 0; col < cells.length; col++) {
                    board.placeSymbol(row, col, cells[col].charAt(0)); // A 'X', 'O' vagy '.' karaktereket beolvassuk
                }
                row++;
            }
        }
        return board;
    }
}
