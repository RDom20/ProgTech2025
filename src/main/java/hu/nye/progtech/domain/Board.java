package hu.nye.progtech.domain;

import java.util.Arrays;

public class Board {
    private final int size;
    private final char[][] cells;
    private char currentPlayer; // új mező

    public Board(int size) {
        this.size = size;
        this.cells = new char[size][size];
        for (int r = 0; r < size; r++) {
            Arrays.fill(this.cells[r], '.');
        }
        this.currentPlayer = 'X'; // alapértelmezett
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(char currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    // ... meglévő metódusok (getCell, placeSymbol, stb.)

    public int getSize() {
        return size;
    }

    public char getCell(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new IndexOutOfBoundsException("Invalid board coordinates");
        }
        return cells[row][col];
    }

    /**
     * Helyez egy szimbólumot a táblára. Ha a mező foglalt, IllegalArgumentException-t dob.
     */
    public void placeSymbol(int row, int col, char symbol) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new IndexOutOfBoundsException("Invalid board coordinates");
        }
        if (cells[row][col] != '.') {
            throw new IllegalArgumentException("Cell is already occupied");
        }
        cells[row][col] = symbol;
    }

    public void setCell(int row, int col, char symbol) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new IndexOutOfBoundsException("Invalid board coordinates");
        }
        cells[row][col] = symbol;
    }

    /**
     * Tesztek számára: a cellák másolatát adja vissza, hogy a teszt ne férjen hozzá közvetlenül a belső tömbhöz.
     */
    public char[][] getCellsCopy() {
        char[][] copy = new char[size][size];
        for (int r = 0; r < size; r++) {
            System.arraycopy(this.cells[r], 0, copy[r], 0, size);
        }
        return copy;
    }
}
