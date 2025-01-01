package assignments.ex2;

import static org.junit.jupiter.api.Assertions.*;

class SCellTest {

    @org.junit.jupiter.api.Test
    void getOrder() {
        SCell formulaWithDependencies = new SCell("=A1+B2");
        assert formulaWithDependencies.getOrder() >= 0;
    }

    @org.junit.jupiter.api.Test
    void testToString() {
        SCell cell = new SCell("123");
        assertEquals("123", cell.toString());
    }

    @org.junit.jupiter.api.Test
    void setData() {
        SCell cell2 = new SCell("=A1+B2");
        cell2.setData("=B3+C4");
        assert cell2.getData().equals("=B3+C4");
        SCell cell = new SCell("Initial");
        cell.setData("Updated");
        assertEquals("Updated", cell.getData());
    }

    @org.junit.jupiter.api.Test
    void getData() {
        SCell cell2 = new SCell("=A1+B2");
        assert cell2.getData().equals("=A1+B2");

        SCell emptyCell = new SCell("");
        SCell nullCell = new SCell(null);
        assert emptyCell.getData().equals("");
        assert emptyCell.getType() == 1;
        assert nullCell.getData() == null;
        assert nullCell.getType() == 1;
    }

    @org.junit.jupiter.api.Test
    void getType() {
        SCell textCell = new SCell("Hello");
        SCell numberCell = new SCell("456");
        SCell formulaCell = new SCell("=A1+4");
        assert textCell.getType() == 1 ;
        assert numberCell.getType() == 2;
        assert formulaCell.getType() == 3;

        SCell cell = new SCell("123");
        assertEquals("123", cell.getData());
        assertEquals(2, cell.getType());
        assertEquals(0, cell.getOrder());
    }

    @org.junit.jupiter.api.Test
    void setType() {
        SCell cell3 = new SCell("Text");
        assert cell3.getType() == 1;
        cell3.setType(3);
        assert cell3.getType() == 3;
    }

    @org.junit.jupiter.api.Test
    void setOrder() {
        SCell cell4 = new SCell("789");
        assert cell4.getOrder() == 0;
        cell4.setOrder(5);
        assert cell4.getOrder() == 5;
    }
}