package hu.nye.progtech.domain;

public class Board {
    private final int size;
    private final char[][] cells;

    public Board(int size) {
        this.size = size;
        cells = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = '.';  // Minden mező üres, '.'
            }
        }
    }

    public char getCell(int row, int col) {
        return cells[row][col];
    }

    public void placeSymbol(int row, int col, char symbol) {
        cells[row][col] = symbol;
    }

    public int getSize() {
        return size;  // Visszaadja a tábla méretét
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(cells[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
