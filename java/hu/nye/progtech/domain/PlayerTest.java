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

    @Test
    void testEqualsHashCodeToStringForPlayer() {
        Player a = new Player("A", 'X');
        Player b = new Player("A", 'X');
        Player c = new Player("C", 'O');

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String s = a.toString();
        assertTrue(s.contains("A"));
        assertTrue(s.contains("X"));
    }
}
