package assignments.ex2;
// Add your documentation below:

public class CellEntry  implements Index2D {
private final char x;
private final int y;

public CellEntry (char a, int b) {
    if (!Character.isLetter(a) || b<0 || b> 99){
        throw new RuntimeException();
    }
    x = a;
    y = b;
}

    @Override
    public boolean isValid() {
    if (!Character.isLetter(x) || y<0 || y> 99) {
        return false;
    }
    return true;
    }

    @Override
    public int getX() {
    return letterToNumber(x);
}

    @Override
    public int getY() {return y;}

    public static int letterToNumber(char letter) {
        return Character.toUpperCase(letter) - 'A';
    }
}
