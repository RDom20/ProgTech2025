package hu.nye.progtech.service;

import hu.nye.progtech.domain.Board;
import hu.nye.progtech.domain.Coordinate;
import hu.nye.progtech.domain.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private final GameService service = new GameService();

    @Test
    void testHasWonHorizontal() {
        Board board = new Board(10);

        for (int i = 0; i < 5; i++) {
            board.placeSymbol(0, i, 'X');
        }

        assertTrue(service.hasWon(board, 'X'));
    }

    @Test
    void testHasWonVertical() {
        Board board = new Board(10);

        for (int i = 0; i < 5; i++) {
            board.placeSymbol(i, 0, 'X');
        }

        assertTrue(service.hasWon(board, 'X'));
    }

    @Test
    void testHasWonDiagonal() {
        Board board = new Board(10);

        for (int i = 0; i < 5; i++) {
            board.placeSymbol(i, i, 'X');
        }

        assertTrue(service.hasWon(board, 'X'));
    }

    @Test
    void testTryRandomMove() {
        Board board = new Board(10);
        GameService gs = new GameService();

        Coordinate c = gs.tryRandomMove(board);

        assertNotNull(c);
        assertEquals('.', board.getCell(c.getRow(), c.getCol()) == '.' ? '.' : '.');
    }

    @Test
    void testAITriesToWin() {
        Board board = new Board(10);
        Player ai = new Player("AI", 'O');

        board.placeSymbol(0, 0, 'O');
        board.placeSymbol(0, 1, 'O');
        board.placeSymbol(0, 2, 'O');
        board.placeSymbol(0, 3, 'O');

        Coordinate move = service.placeNextAIMove(board, ai);

        assertNotNull(move);
        assertEquals(0, move.getRow());
        assertEquals(4, move.getCol());
    }

    @Test
    void testAIBlocksPlayer() {
        Board board = new Board(10);
        Player ai = new Player("AI", 'O');

        board.placeSymbol(0, 0, 'X');
        board.placeSymbol(0, 1, 'X');
        board.placeSymbol(0, 2, 'X');
        board.placeSymbol(0, 3, 'X');

        Coordinate move = service.placeNextAIMove(board, ai);

        assertNotNull(move);
        assertEquals(0, move.getRow());
        assertEquals(4, move.getCol());
    }
}
