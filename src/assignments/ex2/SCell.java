package assignments.ex2;
import java.util.ArrayList;

//SCell represents a single cell in a spreadsheet. Each cell can store text,
// numbers, or formulas and supports computation of dependencies and order of evaluation.
public class SCell implements Cell {
    private String line; //A String storing the content of the cell.
    private int type; //An int representing the type of the cell
    // (for examples 1 for text, 2 for numbers, 3 for formulas).
    private int order; //An int representing the order of computation for this cell.
    private ArrayList<String> depens;


    //Initializes the cell with the given string.
    //Determines the type and order of the cell during initialization
    public SCell(String s) {
        setData(s);
        type = whatType(s);
        order = computeOrder(s);
        depens = new ArrayList<>();
    }

    //Returns the computation order of the cell.
    @Override
    public int getOrder() {
        return order;
    }


    //Returns the cell's content as a string.
    @Override
    public String toString() {
        return getData();
    }

    // Sets the content of the cell.
    @Override
    public void setData(String s) {
        line = s;
    }

    //Returns the content of the cell.
    @Override
    public String getData() {
        return line;
    }

    //Returns the type of the cell.
    @Override
    public int getType() {
        return type;
    }

    //Sets the type of the cell.
    @Override
    public void setType(int t) {
        type = t;
    }

    // Sets the computation order of the cell.
    @Override
    public void setOrder(int t) {
        order = t;

    }

    // Checks if the given string is a valid number.
    public static boolean isNumber(String text) {
        boolean ans;
        try {
            Double.parseDouble(text);
            ans = true;
        } catch (Exception e) {
            ans = false;
        }

        return ans;
    }

    //Checks if the given string is a not number and not formula.
    public static boolean isText(String text) {
        boolean ans = true;
        if (isNumber(text))
            ans = false;
        if (isForm(text))
            ans = false;

        return ans;
    }


    // Checks if the given string is a valid formula.
    public static boolean isForm(String text) {

        if (text == null || text.isEmpty() || text.charAt(0) != '=') {
            return false;
        }

        if (text.charAt(0) == '=') {
            text = text.substring(1);
        }
        text = text.replaceAll("\\s+", "");
        text = text.toUpperCase();

        if (SCell.isNumber(text)) {
            return true;
        }

        char[] Op1 = {'+', '-', '*', '/', '(', ')', '.'};

        char[] op2 = {'+', '-', '*', '/'};

        boolean lastWasOperator = true;
        int parenthesesCount = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            // אם התו הוא ספרה, אין בעיה
            if (Character.isDigit(c)) {
                lastWasOperator = false;
            }

            else if (Character.isLetter(c)) {
                if (validRef(text.substring(i))) {
                    lastWasOperator = false;
                    while (i +1 < text.length() && !containsChar(op2, text.charAt(i+1))){
                        i++;
                    }
                } else {
                    return false;
                }
            }
            // אם זה אופרטור, יש לוודא שאין 2 אופרטורים ברצף
            else if (containsChar(op2, c)) {
                if (lastWasOperator) {
                    return false;
                }
                lastWasOperator = true;
            }
            // אם זה סוגריים, לבדוק שהם מאוזנים
            else if (c == '(') {
                parenthesesCount++;
                lastWasOperator = true; // חייב להיות מספר או אות אחרי סוגריים פתוחים
            }
            else if (c == ')') {
                parenthesesCount--;
                if (parenthesesCount < 0) {
                    return false; // יותר סוגריים סגורים מפתוחים
                }
                lastWasOperator = false; // חייב להיות אופרטור אחרי סוגריים סגורים
            }
            // תו שאינו חוקי
            else {
                return false;
            }
        }

        // לוודא שאין יותר סוגריים פתוחים מאשר סגורים
        if (parenthesesCount != 0) {
            return false;
        }

        // לוודא שהנוסחה לא מסתיימת באופרטור (למשל "=5+")
        return !lastWasOperator;
    }


    private static boolean containsChar(char[] array, char target) {
        for (int i = 0; i < array.length; i++) { // Loop through array with index i
            if (array[i] == target) {
                return true; // Return true if character is found
            }
        }
        return false; // Return false if character is not found
    }


    //Validates if the given string is a reference to another cell.
    public static boolean CellReference(String s) {
        // If no letters are found, it cannot be a valid cell reference.
        if (!s.matches(".*[a-zA-Z].*")) {
            return false;
        }
        // Iterate through each character in the string, check if the character is a letter,
        // if the letter is the second to last or third to last character Get the next character,
        // Return true if it's a digit.
        for (int i = 0; i < s.length(); i++) {
            char x = s.charAt(i);
            if (Character.isLetter(x)) {
                if (i + 1 == s.length() - 1 || i + 2 == s.length() - 1) {
                    char y = s.charAt(i + 1);
                    return Character.isDigit(y);
                }
                // If the letter is followed by more characters
                if (i + 2 < s.length() - 1) {
                    char y = s.charAt(i + 1);
                    char z = s.charAt(i + 2);
                    char a = s.charAt(i + 3);
                    // If the character after the letter is not a digit, return false.
                    if (!Character.isDigit(y)) {
                        return false;
                    }
                    // If there are three consecutive digits after the letter, return false.
                    if (Character.isDigit(y) && Character.isDigit(z) && Character.isDigit(a)) {
                        return false;
                    }
                }
            }

        }
        return true;
    }

    //Determines the type of the content
    public int whatType(String s) {
        if (isText(s)) {
            return 1;
        }
        if (isNumber(s)) {
            return 2;
        }
        if (isForm(s)) {
            return 3;
        }
        return -2;
    }

    //arses the formula to extract cell references that the current cell depends on.
    public static ArrayList<String> Dependencies(String form) {
        ArrayList<String> depen = new ArrayList<>();
        if (form == null || form == "")
            return depen;
        if (form.charAt(0) == '=')
            form = form.substring(1); // Remove the '=' at the beginning of the formula.
        // // Split the formula into array based on mathematical operators and parentheses
        String[] parts = form.split("[+\\-*/()]");
        // Iterate through each part of the formula, check if the part is a valid cell reference,
        // Add the valid cell reference to the dependencies list
        for (int i = 0; i < parts.length; i++) {
            if (CellReference(parts[i])) {
                depen.add(parts[i]);
            }
        }
        return depen;
    }

    //Computes the natural order of this cell based on its dependencies.
    private int computeOrder(String s) {
        // If the cell content is null or empty, the computation order is 0
        if (line == null || line.isEmpty()) {
            return 0;
        }
        // If the cell content is a number or plain text, it doesn't depend on other cells.
        if (isNumber(line) || isText(line)) {
            return 0;
        }
        if (isForm(line)) {
            // Get Arraylist of all dependencies (cell references) in the formula.
            ArrayList<String> depen1 = Dependencies(line);
            int maxorder = 0; // Initialize the maximum order as 0

            // Iterate through the list of dependencies,Create an SCell object for each dependent cell
            // Get the computation order of the dependent cell, and Update the maximum order based on the current dependent cell.
            for (int i = 0; i < depen1.size(); i++) {
                SCell dependentCell = new SCell(depen1.get(i));
                int order = dependentCell.getOrder();
                maxorder = Math.max(maxorder, order);
            }
            return maxorder + 1; // The order of this cell is 1 plus the maximum order of its dependencies.

        }
        // If the content doesn't match any valid type, return -1 (indicates an error)
        return -1;
    }

    private static boolean validRef(String s) {
        char[] c2 = {'+', '-', '*', '/', ')', '.'};
        char x = s.charAt(0);
        if (s.length() == 1) {
            return false;
        }
        if (s.length() == 2) {
            char y = s.charAt(1);
            if (!Character.isDigit(y)) {
                return false;
            }
        }
        if (s.length() == 3) {
            char y = s.charAt(1);
            char z = s.charAt(2);
            if (!Character.isDigit(y) || (!Character.isDigit(z) && z != ')')) {
                return false;
            }
        }
        // If the letter is followed by more characters
        if (s.length() > 3) {
            char y = s.charAt(1);
            char z = s.charAt(2);
            char a = s.charAt(3);
            // If the character after the letter is not a digit, return false.
            if (!Character.isDigit(y)) {
                return false;
            }
            // if z isn't digit.
            if (!Character.isDigit(z)) {
                int count = 0;
                if (!containsChar(c2, z)) {
                    return false;
                }
                // max index can be 99
                if (Character.isDigit(z) && Character.isDigit(a)) {
                    return false;
                }
            }
        }
        return true;
    }
}
