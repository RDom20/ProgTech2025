package hu.nye.progtech.domain;

public class Board {
    private int size;
    private char[][] cells;
    private char currentPlayer;

    public Board(int size) {
        this.size = size;
        this.cells = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = '.';
            }
        }
        currentPlayer = 'X';
    }

    public int getSize() {
        return size;
    }

    public char getCell(int row, int col) {
        return cells[row][col];
    }

    public void setCell(int row, int col, char symbol) {
        cells[row][col] = symbol;
    }

    public void placeSymbol(int row, int col, char symbol) {
        cells[row][col] = symbol;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(char player) {
        this.currentPlayer = player;
    }
}
