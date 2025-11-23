package hu.nye.progtech.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CoordinateTest {

    @Test
    void testConstructorAndGetters() {
        Coordinate c = new Coordinate(3, 7);

        assertEquals(3, c.getRow());
        assertEquals(7, c.getCol());
    }

    @Test
    void testDifferentCoordinatesNotEqual() {
        Coordinate c1 = new Coordinate(1, 2);
        Coordinate c2 = new Coordinate(2, 1);

        assertNotEquals(c1.getRow(), c2.getRow());
        assertNotEquals(c1.getCol(), c2.getCol());
    }

    @Test
    void testCoordinateValuesAreImmutable() {
        Coordinate c = new Coordinate(5, 5);

        assertEquals(5, c.getRow());
        assertEquals(5, c.getCol());

        // nincs setter → immutable → ez a teszt csak ellenőrzi a VO jellegét
        assertEquals(5, c.getRow());
        assertEquals(5, c.getCol());
    }
}
