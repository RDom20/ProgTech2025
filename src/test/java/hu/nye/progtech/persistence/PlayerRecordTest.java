package hu.nye.progtech.persistence;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerRecordTest {

    @Test
    void testConstructorsAndGettersSetters() {
        PlayerRecord pr1 = new PlayerRecord();
        pr1.setId(1L);
        pr1.setName("Alice");
        pr1.setWins(3);

        assertEquals(1L, pr1.getId());
        assertEquals("Alice", pr1.getName());
        assertEquals(3, pr1.getWins());

        PlayerRecord pr2 = new PlayerRecord("Bob", 2);
        assertNull(pr2.getId());
        assertEquals("Bob", pr2.getName());
        assertEquals(2, pr2.getWins());

        PlayerRecord pr3 = new PlayerRecord(5L, "Carol", 7);
        assertEquals(5L, pr3.getId());
        assertEquals("Carol", pr3.getName());
        assertEquals(7, pr3.getWins());
    }

    @Test
    void testEqualsHashCodeToString() {
        PlayerRecord a = new PlayerRecord(1L, "Alice", 4);
        PlayerRecord b = new PlayerRecord(1L, "Alice", 4);
        PlayerRecord c = new PlayerRecord(2L, "Bob", 1);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String s = a.toString();
        assertTrue(s.contains("Alice"));
        assertTrue(s.contains("4"));
    }
}
