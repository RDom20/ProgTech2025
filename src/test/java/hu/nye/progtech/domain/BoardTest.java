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
}
