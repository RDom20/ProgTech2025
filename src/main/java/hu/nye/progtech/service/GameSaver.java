package hu.nye.progtech.service;

import hu.nye.progtech.domain.Board;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GameSaver {

    public void saveGame(Board board, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int row = 0; row < board.getSize(); row++) {
                for (int col = 0; col < board.getSize(); col++) {
                    writer.write(board.getCell(row, col) + " ");
                }
                writer.newLine();
            }
        }
    }
}
