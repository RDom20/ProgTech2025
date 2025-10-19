package hu.nye.progtech.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board(10); // 10x10-es tábla
    }

    @Test
    public void testInitialBoardEmpty() {
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                assertEquals('.', board.getCell(row, col), "Board-nak alapból üres kell lennie");
            }
        }
    }

    @Test
    public void testPlaceSymbol() {
        board.placeSymbol(0, 0, 'X');
        assertEquals('X', board.getCell(0, 0), "Cell-nek 'X' szimbólumot kellene tartalmaznia");
    }

    @Test
    public void testToString() {
        board.placeSymbol(0, 0, 'X');
        String boardString = board.toString();
        assertTrue(boardString.contains("X"), "Board string-ben benne kellene lennie az 'X' szimbólumnak, ha az helyesen volt elhelyezve");
    }
}
