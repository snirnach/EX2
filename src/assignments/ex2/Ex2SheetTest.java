package assignments.ex2;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class Ex2SheetTest {
    @Test
    public void testCreateSheet() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertEquals(5, sheet.width());
        assertEquals(5, sheet.height());
    }

    @Test
    public void testSetAndGetCell() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(1, 1, "42");
        assertEquals("42", sheet.get(1, 1).getData());
    }

    @Test
    public void testGetCellByReference() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(2, 2, "form");
        assertEquals("form", sheet.get("C2").getData());
    }

    @Test
    public void testValue() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "5");
        sheet.set(1, 1, "=A0*2");
        assertEquals("5.0", sheet.value(0, 0));
        assertEquals("10.0", sheet.value(1, 1));
    }

    @Test
    public void testComputeFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertEquals(8.0, sheet.computeForm("=4+4"));
        assertEquals(15.0, sheet.computeForm("=5*3"));
        assertEquals(6.0, sheet.computeForm("=2+2*2"));
        assertEquals(15.0, sheet.computeForm("=(2+3)*3"));
        assertEquals(1.25, sheet.computeForm("=5/(2+2)"));
        assertEquals(10.0, sheet.computeForm("=(3+7)"));
        assertEquals(14.0, sheet.computeForm("=2*(3+4)"));
    }

    @Test
    public void testCircularReferenceDetection() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(1, 1, "=8");
        sheet.set(0, 1, "=B1");
        sheet.set(1, 1, "=A1");
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 1));
    }

    @Test
    public void testIsInBounds() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertTrue(sheet.isIn(0, 0));
        assertFalse(sheet.isIn(6, 6));
        assertFalse(sheet.isIn(-1, 0));
    }

    @Test
    public void testLoadAndSave() throws Exception {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(1, 1, "100");
        sheet.save("testSheet.csv");

        Ex2Sheet loadedSheet = new Ex2Sheet(5, 5);
        loadedSheet.load("testSheet.csv");
        assertEquals("100.0", loadedSheet.value(1, 1));
    }

    @Test
    void testEval() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(1, 1, "3");
        sheet.set(2, 2, "2");
        sheet.set(3, 3, "=B1 + C2 * 2");
        assertEquals("7.0", sheet.eval(3, 3));
        sheet.set(0, 0, "=4+6");
        assertEquals("10.0", sheet.eval(0, 0));
        sheet.set(1, 1, "=2*3");
        assertEquals("6.0", sheet.eval(1, 1));

        Ex2Sheet sheet1 = new Ex2Sheet(5, 5);
        sheet1.set(0, 0, "=A0");
        assertEquals(Ex2Utils.ERR_CYCLE, sheet1.eval(0, 0));
    }
@Test
    public void testComputeOrder() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertEquals(0, sheet.computeOrder("5", 0, 0, new HashSet<>()));
        assertEquals(0, sheet.computeOrder("3.14", 0, 0, new HashSet<>()));
        assertEquals(0, sheet.computeOrder("sf", 0, 0, new HashSet<>()));
        assertEquals(0, sheet.computeOrder("test", 0, 0, new HashSet<>()));
        assertEquals(1, sheet.computeOrder("=5+3", 0, 0, new HashSet<>()));
        assertEquals(1, sheet.computeOrder("=4*2", 0, 0, new HashSet<>()));
        sheet.set(1, 1, "5");
        sheet.set(2, 2, "=B1+3");
        assertEquals(1, sheet.computeOrder("=B1+3", 2, 2, new HashSet<>()));
        sheet.set(1, 1, "2");
        sheet.set(2, 2, "=B1+1");
        sheet.set(3, 3, "=C2*2");
        assertEquals(2, sheet.computeOrder("=C2*2", 3, 3, new HashSet<>()));
        sheet.set(0, 0, "=A0");
        assertEquals(-1, sheet.computeOrder("=A0", 0, 0, new HashSet<>()));
        sheet.set(0, 0, "1");
        sheet.set(1, 1, "=A0+1");
        sheet.set(2, 2, "=B1+1");
        sheet.set(3, 3, "=C2+1");
        sheet.set(4, 4, "=D3+1");
        assertEquals(4, sheet.computeOrder("=D3+1", 4, 4, new HashSet<>()));
        sheet.set(1, 1, "3");
        sheet.set(2, 2, "=B1*2");
        sheet.set(3, 3, "=C2+B1");
        sheet.set(4, 4, "=C2*D3");
        assertEquals(1, sheet.computeOrder("=B1*2", 2, 2, new HashSet<>()));
        assertEquals(2, sheet.computeOrder("=C2+B1", 3, 3, new HashSet<>()));
        assertEquals(3, sheet.computeOrder("=C2*D3", 4, 4, new HashSet<>()));
    }
}