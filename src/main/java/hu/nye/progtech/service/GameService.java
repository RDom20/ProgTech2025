package hu.nye.progtech.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hu.nye.progtech.domain.Board;
import hu.nye.progtech.domain.Coordinate;
import hu.nye.progtech.domain.Player;

/**
 * Játékszolgáltatások: nyerés ellenőrzés, AI lépés választás.
 */
public class GameService {
    private static final int WIN_LENGTH = 5;
    private final Random random = new Random();

    // alapirányok (négy fő irány) - a diagonális ellenőrzés külön metódusban van
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    public boolean hasWon(Board board, char symbol) {
        int size = board.getSize();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board.getCell(row, col) == symbol) {
                    for (int[] direction : DIRECTIONS) {
                        if (checkDirection(board, row, col, direction[0], direction[1], symbol)) {
                            return true;
                        }
                    }
                    if (checkDiagonal(board, row, col, symbol)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkDiagonal(Board board, int row, int col, char symbol) {
        final int size = board.getSize();

        // bal-fel / jobb-le átló
        int count = 1;
        int r = row - 1;
        int c = col - 1;
        while (r >= 0 && c >= 0 && board.getCell(r, c) == symbol) {
            count++;
            r--;
            c--;
        }
        r = row + 1;
        c = col + 1;
        while (r < size && c < size && board.getCell(r, c) == symbol) {
            count++;
            r++;
            c++;
        }
        if (count >= WIN_LENGTH) {
            return true;
        }

        // jobb-fel / bal-le átló
        count = 1;
        r = row - 1;
        c = col + 1;
        while (r >= 0 && c < size && board.getCell(r, c) == symbol) {
            count++;
            r--;
            c++;
        }
        r = row + 1;
        c = col - 1;
        while (r < size && c >= 0 && board.getCell(r, c) == symbol) {
            count++;
            r++;
            c--;
        }
        return count >= WIN_LENGTH;
    }

    private boolean checkDirection(Board board, int row, int col, int rowInc, int colInc, char symbol) {
        int size = board.getSize();
        int count = 1;
        for (int i = 1; i < WIN_LENGTH; i++) {
            int newRow = row + i * rowInc;
            int newCol = col + i * colInc;
            if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size && board.getCell(newRow, newCol) == symbol) {
                count++;
            } else {
                break;
            }
        }
        return count >= WIN_LENGTH;
    }

    public Coordinate placeNextAIMove(Board board, Player ai) {
        Coordinate aiMove = tryToWin(board, ai);
        if (aiMove == null) {
            aiMove = tryToBlockPlayer(board, ai);
        }
        return aiMove != null ? aiMove : tryRandomMove(board);
    }

    public Coordinate tryRandomMove(Board board) {
        List<Coordinate> emptyCells = new ArrayList<>();
        int size = board.getSize();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board.getCell(row, col) == '.') {
                    emptyCells.add(new Coordinate(row, col));
                }
            }
        }
        if (!emptyCells.isEmpty()) {
            return emptyCells.get(random.nextInt(emptyCells.size()));
        }
        return null;
    }

    private Coordinate tryToWin(Board board, Player ai) {
        int size = board.getSize();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board.getCell(row, col) == '.') {
                    // szimuláljuk a lépést setCell-el (nem placeSymbol), majd visszaállítjuk
                    board.setCell(row, col, ai.getSymbol());
                    boolean win = hasWon(board, ai.getSymbol());
                    board.setCell(row, col, '.'); // visszaállítás
                    if (win) {
                        return new Coordinate(row, col);
                    }
                }
            }
        }
        return null;
    }

    private Coordinate tryToBlockPlayer(Board board, Player ai) {
        char playerSymbol = ai.getSymbol() == 'X' ? 'O' : 'X';
        int size = board.getSize();

        // teljes iránykészlet (8 irány) a blokkoláshoz
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {1, 1}, {-1, 1}, {1, -1}
        };

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board.getCell(row, col) == playerSymbol) {
                    for (int[] direction : directions) {
                        int count = 1;
                        List<Coordinate> potential = new ArrayList<>();
                        for (int i = 1; i < WIN_LENGTH; i++) {
                            int nr = row + i * direction[0];
                            int nc = col + i * direction[1];
                            if (nr >= 0 && nr < size && nc >= 0 && nc < size) {
                                char cell = board.getCell(nr, nc);
                                if (cell == playerSymbol) {
                                    count++;
                                } else if (cell == '.') {
                                    potential.add(new Coordinate(nr, nc));
                                } else {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (count >= 2 && !potential.isEmpty()) {
                            for (Coordinate c : potential) {
                                if (board.getCell(c.getRow(), c.getCol()) == '.') {
                                    // használjunk setCell-et a tényleges lépéshez (nem placeSymbol), vagy ha a Game logikája
                                    // elvárja a placeSymbol viselkedést, akkor itt a Game-nek kell meghívnia placeSymbol-t.
                                    board.setCell(c.getRow(), c.getCol(), ai.getSymbol());
                                    return new Coordinate(c.getRow(), c.getCol());
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
