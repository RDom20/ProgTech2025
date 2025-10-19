package hu.nye.progtech.domain;

public class Board {
    private char[][] grid;
    private int size;

    public Board(int size) {
        this.size = size;
        grid = new char[size][size];
        // Initialize the board with empty cells
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = '.'; // '.' represents an empty cell
            }
        }
    }

    public int getSize() {
        return size;
    }

    public char getCell(int row, int col) {
        return grid[row][col];
    }

    // Add the setCell method to set the value of a specific cell
    public void setCell(int row, int col, char symbol) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            grid[row][col] = symbol;
        } else {
            System.out.println("Invalid position!");
        }
    }

    // Add the placeSymbol method to allow placing symbols on the board
    public void placeSymbol(int row, int col, char symbol) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            grid[row][col] = symbol;
        } else {
            System.out.println("Invalid position!");
        }
    }

    // Optional: You can also add a method to print the board for debugging
    public void printBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
}

