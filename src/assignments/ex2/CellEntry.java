package assignments.ex2;
// The CellEntry class represents a 2D cell entry in a grid,
// identified by a character (x, typically a column) and an integer (y, typically a row).
// It implements the Index2D interface and provides methods
// to validate the cell and retrieve its coordinates in numerical format.

public class CellEntry  implements Index2D {
private final char x;//The letter representing the column of the cell.
private final int y;//The number representing the row of the cell. Must be between 0 and 99.

    //Initializes a CellEntry object with the given column (a) and row (b).
public CellEntry (char a, int b) {
    if (!Character.isLetter(a) || b<0 || b> 99){
        throw new RuntimeException();
    }
    x = a;
    y = b;
}

//Checks if the cell entry is valid.
    @Override
    public boolean isValid() {
    //The column (x) must be a letter. The row (y) must be between 0 and 99.
    if (!Character.isLetter(x) || y<0 || y> 99) {
        return false;
    }
    return true;
    }

    //Converts the letter (A-Z or a-z) to a 0-based index using the letterToNumber method.
    @Override
    public int getX() {
    return letterToNumber(x);
}

    //Retrieves the row value (y).
    @Override
    public int getY() {return y;}

    //Converts a letter to its 0-based index
    public static int letterToNumber(char letter) {
        return Character.toUpperCase(letter) - 'A';
    }
}
