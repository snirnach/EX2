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

        CellEntry entry = new CellEntry('A', 5);
        assertTrue(entry.isValid());

        CellEntry invalidEntry1 = new CellEntry('Z', 100);
        assertFalse(invalidEntry1.isValid());

        CellEntry invalidEntry2 = new CellEntry('1', 5);
        assertFalse(invalidEntry2.isValid());
    }

    @Test
    public void testGetX() {
        CellEntry entry = new CellEntry('B', 10);
        assertEquals(1, entry.getX()); // 'B' should map to 1 (0-based index)

        CellEntry invalidEntry = new CellEntry('1', 10);
        assertEquals(Ex2Utils.ERR, invalidEntry.getX());
    }

    @Test
    public void testGetY() {
        CellEntry entry = new CellEntry('C', 25);
        assertEquals(25, entry.getY());

        CellEntry invalidEntry = new CellEntry('C', 150);
        assertEquals(Ex2Utils.ERR, invalidEntry.getY());
    }

    @Test
    public void testLetterToNumber() {
        assertEquals(0, CellEntry.letterToNumber('A'));
        assertEquals(1, CellEntry.letterToNumber('B'));
        assertEquals(25, CellEntry.letterToNumber('Z'));
        assertEquals(0, CellEntry.letterToNumber('a'));
        assertEquals(25, CellEntry.letterToNumber('z'));
    }
}