package hu.nye.progtech.service;

import hu.nye.progtech.domain.Board;
import hu.nye.progtech.domain.Coordinate;
import hu.nye.progtech.domain.Player;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GameServiceTest {

    private final GameService service = new GameService();

    @ParameterizedTest
    @ValueSource(ints = {5, 6, 10})
    void hasWonHorizontalVariousSizes(int size) {
        Board b = new Board(size);
        for (int i = 0; i < 5; i++) b.placeSymbol(0, i, 'X');
        assertTrue(service.hasWon(b, 'X'));
    }

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
        Coordinate c = service.tryRandomMove(board);
        assertNotNull(c);
        assertEquals('.', board.getCell(c.getRow(), c.getCol()));
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

    @Test
    void testTryRandomMoveMultipleTimesDoesNotReturnNull() {
        Board b = new Board(3);
        b.placeSymbol(0,0,'X');
        b.placeSymbol(0,1,'O');
        b.placeSymbol(0,2,'X');
        for (int i = 0; i < 10; i++) {
            Coordinate c = service.tryRandomMove(b);
            assertNotNull(c);
            assertEquals('.', b.getCell(c.getRow(), c.getCol()));
        }
    }

    @Test
    void testPlaceNextAIMovePrefersWinning() {
        Board b = new Board(10);
        Player ai = new Player("AI", 'O');
        for (int i = 0; i < 4; i++) b.placeSymbol(5, i, 'O');
        Coordinate move = service.placeNextAIMove(b, ai);
        assertNotNull(move);
        assertEquals(5, move.getRow());
        b.placeSymbol(move.getRow(), move.getCol(), 'O');
        assertTrue(service.hasWon(b, 'O'));
    }

    @Test
    void testWinDetectionVariousPatterns() {
        Board b = new Board(5);
        for (int i = 0; i < 4; i++) b.placeSymbol(2, i, 'X');
        assertFalse(service.hasWon(b, 'X'));
        b.placeSymbol(2,4,'X');
        assertTrue(service.hasWon(b, 'X'));
    }

    @Test
    void tryToWinCompletesRow() {
        Board b = new Board(5);
        Player ai = new Player("AI",'O');
        for (int i=0;i<4;i++) b.setCell(1,i,'O');
        var move = service.placeNextAIMove(b, ai);
        assertNotNull(move);
        b.setCell(move.getRow(), move.getCol(), 'O');
        assertTrue(service.hasWon(b, 'O'));
    }

    @Test
    void tryToBlockDetectsOpponent() {
        Board b = new Board(5);
        Player ai = new Player("AI",'O');
        for (int i=0;i<4;i++) b.setCell(2,i,'X');
        var move = service.placeNextAIMove(b, ai);
        assertNotNull(move);
        b.setCell(move.getRow(), move.getCol(), 'O');
        assertFalse(service.hasWon(b, 'X'));
    }

}
