package assignments.ex2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellEntryTest {

    @Test
    void isValid() {
        CellEntry cell = new CellEntry('A', 5);
        assertTrue(cell.isValid());
        assertEquals(0, cell.getX());
        assertEquals(5, cell.getY());

        CellEntry cell1 = new CellEntry('b', 7);
        assertTrue(cell.isValid());
        assertEquals(1, cell1.getX());
        assertEquals(7, cell1.getY());

        CellEntry cell2 = new CellEntry('C', 0);
        assertTrue(cell2.isValid());
        assertEquals(2, cell2.getX());
        assertEquals(0, cell2.getY());

        CellEntry cell3 = new CellEntry('D', 99);
        assertTrue(cell3.isValid());
        assertEquals(3, cell3.getX());
        assertEquals(99, cell3.getY());

    }

    @Test
    void getX() {

    }

    @Test
    void getY() {
    }
}