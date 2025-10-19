package hu.nye.progtech.service;

import hu.nye.progtech.domain.Board;
import hu.nye.progtech.domain.Coordinate;
import hu.nye.progtech.domain.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameService {
    private static final int WIN_LENGTH = 4;  // Nyerési hossz
    private final Random random = new Random();
    private static final int MAP_SIZE = 10;  // Tábla mérete (10x10-es tábla)

    private static final int[][] DIRECTIONS = {
            {-1, 0}, // fel
            {1, 0},  // le
            {0, -1}, // balra
            {0, 1}   // jobbra
    };

    public boolean hasWon(Board board, char symbol) {
        // Ellenőrizzük, hogy van-e nyerő sorozat
        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                if (board.getCell(row, col) == symbol) {
                    // Minden irányt végigellenőrzünk
                    for (int[] direction : DIRECTIONS) {
                        if (checkDirection(board, row, col, direction[0], direction[1], symbol)) {
                            return true;  // Nyert
                        }
                    }

                    // Ellenőrizzük az átlókat
                    if (checkDiagonal(board, row, col, symbol)) {
                        return true;  // Nyert átlósan
                    }
                }
            }
        }
        return false;  // Ha nincs nyerő sorozat
    }

    private boolean checkDiagonal(Board board, int row, int col, char symbol) {
        // Balról jobbra átló (felfelé-balra)
        int count = 1;
        int r = row - 1, c = col - 1;
        while (r >= 0 && c >= 0 && board.getCell(r, c) == symbol) {
            count++;
            r--;
            c--;
        }
        r = row + 1;
        c = col + 1;
        while (r < MAP_SIZE && c < MAP_SIZE && board.getCell(r, c) == symbol) {
            count++;
            r++;
            c++;
        }
        if (count >= WIN_LENGTH) return true;  // Ha találtunk 4-et

        // Jobbról balra átló (lefelé-jobbra)
        count = 1;
        r = row + 1;
        c = col - 1;
        while (r < MAP_SIZE && c >= 0 && board.getCell(r, c) == symbol) {
            count++;
            r++;
            c--;
        }
        r = row - 1;
        c = col + 1;
        while (r >= 0 && c < MAP_SIZE && board.getCell(r, c) == symbol) {
            count++;
            r--;
            c++;
        }
        return count >= WIN_LENGTH;  // Ha találtunk 4-et
    }


    private boolean checkDirection(Board board, int row, int col, int rowIncrement, int colIncrement, char symbol) {
        int count = 1;
        for (int i = 1; i < WIN_LENGTH; i++) {
            int newRow = row + i * rowIncrement;
            int newCol = col + i * colIncrement;
            if (newRow >= 0 && newRow < MAP_SIZE && newCol >= 0 && newCol < MAP_SIZE && board.getCell(newRow, newCol) == symbol) {
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
        return aiMove != null ? aiMove : tryRandomMove(board);  // Ha nincs nyerő vagy blokkoló lépés, véletlen lépés
    }

    public Coordinate tryRandomMove(Board board) {
        List<Coordinate> emptyCells = new ArrayList<>();
        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                if (board.getCell(row, col) == '.') {  // Üres mező ellenőrzése
                    emptyCells.add(new Coordinate(row, col));  // Üres mező hozzáadása
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            return emptyCells.get(random.nextInt(emptyCells.size()));  // Véletlenszerűen választunk egy üres mezőt
        }
        return null;
    }

    private Coordinate tryToWin(Board board, Player ai) {
        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                if (board.getCell(row, col) == '.') {
                    board.placeSymbol(row, col, ai.getSymbol());
                    if (hasWon(board, ai.getSymbol())) {
                        return new Coordinate(row, col);
                    }
                    board.placeSymbol(row, col, '.');  // Visszavonjuk a lépést
                }
            }
        }
        return null;
    }

    private Coordinate tryToBlockPlayer(Board board, Player ai) {
        char playerSymbol = ai.getSymbol() == 'X' ? 'O' : 'X';  // Ha AI 'X', akkor a játékos 'O', és fordítva

        // Négy irányban kell keresni: vízszintes, függőleges, balról jobbra átló, jobbról balra átló
        int[][] directions = {
                {-1, 0},  // függőleges felfelé
                {1, 0},   // függőleges lefelé
                {0, -1},  // vízszintes balra
                {0, 1},   // vízszintes jobbra
                {-1, -1}, // balról jobbra átló (felfelé-balra)
                {1, 1}    // jobbról balra átló (lefelé-jobbra)
        };

        // Végigmegyünk a táblán
        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                if (board.getCell(row, col) == playerSymbol) {  // Ha a játékosnak van egy jele a mezőn
                    // Minden irányt megnézünk
                    for (int[] direction : directions) {
                        int count = 1;  // Már megtaláltunk egy jelet, most keresünk többit
                        List<Coordinate> potentialBlockCoordinates = new ArrayList<>();

                        // Keressük a sorozatot a megfelelő irányban
                        for (int i = 1; i < WIN_LENGTH; i++) {
                            int newRow = row + i * direction[0];
                            int newCol = col + i * direction[1];

                            // Ha érvényes a mező
                            if (newRow >= 0 && newRow < MAP_SIZE && newCol >= 0 && newCol < MAP_SIZE) {
                                if (board.getCell(newRow, newCol) == playerSymbol) {
                                    count++;  // Növeljük a sorozatot
                                } else if (board.getCell(newRow, newCol) == '.') {
                                    // Ha üres mezőt találunk a sorozat végén, hozzáadjuk a potenciális blokkolási helyekhez
                                    potentialBlockCoordinates.add(new Coordinate(newRow, newCol));
                                } else {
                                    break;  // Ha egy másik jel jön, megszakítjuk a keresést
                                }
                            } else {
                                break;  // Ha kimegyünk a tábláról, megállunk
                            }
                        }

                        // Ha legalább 2 jelet találtunk és van üres mező, ahol blokkot tehetünk
                        if (count >= 2 && !potentialBlockCoordinates.isEmpty()) {
                            for (Coordinate coordinate : potentialBlockCoordinates) {
                                int nextRow = coordinate.getRow();
                                int nextCol = coordinate.getCol();

                                // Biztosítjuk, hogy a következő mező üres legyen
                                if (nextRow >= 0 && nextRow < MAP_SIZE && nextCol >= 0 && nextCol < MAP_SIZE
                                        && board.getCell(nextRow, nextCol) == '.') {
                                    board.placeSymbol(nextRow, nextCol, ai.getSymbol());
                                    return new Coordinate(nextRow, nextCol);  // Visszaadjuk a blokk lépést
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;  // Ha nem találunk blokkolható helyet
    }
}