package hu.nye.progtech.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    public void testPlayerCreation() {
        Player player = new Player("Teszt", 'X');
        assertEquals("Teszt", player.getName(), "Player neve 'Teszt'-nek kellene lennie");
        assertEquals('X', player.getSymbol(), "Player szimbólumnak 'X'-nek kellene lennie");
    }
}
