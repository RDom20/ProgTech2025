package hu.nye.progtech.domain;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void testBoardInitialization() {
        Board board = new Board(10);

        assertEquals(10, board.getSize());
        assertEquals('X', board.getCurrentPlayer());

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                assertEquals('.', board.getCell(r, c));
            }
        }
    }

    @Test
    void testSetAndGetCell() {
        Board board = new Board(10);
        board.setCell(2, 3, 'X');

        assertEquals('X', board.getCell(2, 3));
    }

    @Test
    void testSetCurrentPlayer() {
        Board board = new Board(10);
        board.setCurrentPlayer('O');

        assertEquals('O', board.getCurrentPlayer());
    }

    @Test
    void testPlaceSymbolOutOfBoundsThrows() {
        Board b = new Board(5);
        assertThrows(IndexOutOfBoundsException.class, () -> b.placeSymbol(-1, 0, 'X'));
        assertThrows(IndexOutOfBoundsException.class, () -> b.placeSymbol(0, 5, 'X'));
    }

    @Test
    void testPlaceSymbolOnOccupiedThrows() {
        Board b = new Board(3);
        b.placeSymbol(1,1,'X');
        assertThrows(IllegalArgumentException.class, () -> b.placeSymbol(1,1,'O'));
    }

    void placeSymbolVarious(int r, int c, char s) {
        Board b = new Board(5);
        b.placeSymbol(r,c,s);
        assertEquals(s, b.getCell(r,c));
    }

    @Test
    void testGetCellsCopyIsIndependent() {
        Board b = new Board(3);
        char[][] copy = b.getCellsCopy();
        copy[0][0] = 'Z';
        assertEquals('.', b.getCell(0,0));
    }
}
