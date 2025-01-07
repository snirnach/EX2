package assignments.ex2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SCellTest {
    @Test
    public void testSetData() {
        SCell cell = new SCell("Hello");
        assertEquals("Hello", cell.getData());

        cell.setData("123");
        assertEquals("123", cell.getData());
    }

    @Test
    public void testToString() {
        SCell cell = new SCell("Example");
        assertEquals("Example", cell.toString());

        cell.setData("New Data");
        assertEquals("New Data", cell.toString());
    }

    @Test
    public void testGetTypeAndSetType() {
        SCell cell = new SCell("Example");
        cell.setType(1);
        assertEquals(1, cell.getType());

        cell.setType(2);
        assertEquals(2, cell.getType());

        cell.setType(3);
        assertEquals(3, cell.getType());
    }

    @Test
    void isNumber() {
        assertTrue(SCell.isNumber("123"));
        assertTrue(SCell.isNumber("3.14"));
        assertTrue(SCell.isNumber("-5.67"));
        assertTrue(SCell.isNumber("0"));

        assertFalse(SCell.isNumber("abc"));
        assertFalse(SCell.isNumber("12abc"));
        assertFalse(SCell.isNumber("3.14.15"));
        assertFalse(SCell.isNumber(""));
        assertFalse(SCell.isNumber(null));
    }

    @Test
    void isText() {
        assertTrue(SCell.isText("Snir"));
        assertTrue(SCell.isText("A1+B2"));
        assertTrue(SCell.isText("1.2.3"));
        assertTrue(SCell.isText("jdfghdfj"));

        assertFalse(SCell.isText("123"));
        assertFalse(SCell.isText("-4.56"));
        assertFalse(SCell.isText("3.1415"));
        assertFalse(SCell.isText("=a1+B6"));
    }

    @Test
    void isForm() {
        assertTrue(SCell.isForm("=5+3"));
        assertTrue(SCell.isForm("=A1+B2"));
        assertTrue(SCell.isForm("=(5*3)/2"));
        assertTrue(SCell.isForm("=5.5+4.2"));
        assertTrue(SCell.isForm("=A2*B3/C4"));
        assertTrue(SCell.isForm("=(A1+B1)*(C1+D1)"));
        assertTrue(SCell.isForm("=((A1+5)*2)/C3"));

        assertFalse(SCell.isForm(null));
        assertFalse(SCell.isForm(""));
        assertFalse(SCell.isForm("5+3"));
        assertFalse(SCell.isForm("=+5-3"));
        assertFalse(SCell.isForm("=A1+"));
        assertFalse(SCell.isForm("=A1B2"));
        assertFalse(SCell.isForm("=5..2+3"));
        assertFalse(SCell.isForm("=5+*3"));
        assertFalse(SCell.isForm("=(5+3"));
        assertFalse(SCell.isForm("=5+3)"));
        assertFalse(SCell.isForm("=SUM(A1 A3)"));
    }

    @Test
    void cellReference() {
        assertTrue(SCell.CellReference("A1"));
        assertTrue(SCell.CellReference("B12"));
        assertTrue(SCell.CellReference("Z99"));
        assertTrue(SCell.CellReference("M5"));
        assertTrue(SCell.CellReference("C3"));
        assertTrue(SCell.CellReference("A0"));

        assertFalse(SCell.CellReference(null));
        assertFalse(SCell.CellReference(""));
        assertFalse(SCell.CellReference("5A"));
        assertFalse(SCell.CellReference("A"));
        assertFalse(SCell.CellReference("AA12"));
        assertFalse(SCell.CellReference("A123"));
        assertFalse(SCell.CellReference("AB12"));
        assertFalse(SCell.CellReference("B12X"));
        assertFalse(SCell.CellReference("C-5"));
    }

}