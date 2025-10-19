package hu.nye.progtech.service;

import hu.nye.progtech.domain.Board;
import hu.nye.progtech.domain.Coordinate;
import hu.nye.progtech.domain.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;
    private Board board;
    private Player playerX;
    private Player playerO;

    @BeforeEach
    public void setUp() {
        board = new Board(10); // 10x10-es tábla
        gameService = new GameService();
        playerX = new Player("PlayerX", 'X');
        playerO = new Player("PlayerO", 'O');
    }

    @Test
    public void testHasWonVertical() {
        // Teszteljük, hogy van-e nyerő függőleges sorozat
        board.placeSymbol(0, 0, 'X');
        board.placeSymbol(1, 0, 'X');
        board.placeSymbol(2, 0, 'X');
        board.placeSymbol(3, 0, 'X');
        assertTrue(gameService.hasWon(board, 'X'), "Player X-nek nyernie kellett volna függőlegesen");
    }

    @Test
    public void testHasWonHorizontal() {
        // Teszteljük, hogy van-e nyerő vízszintes sorozat
        board.placeSymbol(0, 0, 'O');
        board.placeSymbol(0, 1, 'O');
        board.placeSymbol(0, 2, 'O');
        board.placeSymbol(0, 3, 'O');
        assertTrue(gameService.hasWon(board, 'O'), "Player O-nak nyernie kellett volna vízszintesen");
    }

    @Test
    public void testInvalidMove() {
        board.placeSymbol(0, 0, 'X');
        gameService.placeNextAIMove(board, playerO);  // AI lépése
        assertEquals('X', board.getCell(0, 0), "Cell (0, 0) még mindig 'X'-nek kellene hogy legyen hisz AI nem írhatja felül");
    }
}
