package assignments.ex2;

//SCell represents a single cell in a spreadsheet. Each cell can store text,
// numbers, or formulas and supports computation of dependencies and order of evaluation.
public class SCell implements Cell {
    private String line; //A String storing the content of the cell.
    private int type; //An int representing the type of the cell
    // (for examples 1 for text, 2 for numbers, 3 for formulas).
    private int order; //An int representing the order of computation for this cell.


    //Initializes the cell with the given string.
    //Determines the type and order of the cell during initialization
    public SCell(String s) {
        setData(s);
        type = whatType(s);
        order= 0;
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
        if (text== null || text.isEmpty()){
            return false;
        }
        if (isNumber(text))
            ans = false;
        if ( text.charAt(0) == '=')
            ans = false;

        return ans;
    }



    public static boolean isForm(String text) {
        if (text == null || text.isEmpty() || text.charAt(0) != '=') {
            return false;
        }

        text = text.substring(1);
        text = text.replaceAll("\\s+", "").toUpperCase();

        if (SCell.isNumber(text)) {
            return true;
        }

        char[] op1 = {'+', '-', '*', '/', '(', '.'};
        char[] op2 = {'+', '-', '*', '/'};
        char[] op3 = {'+', '-', '*', '/', ')'};

        if (containsChar(op1, text.charAt(text.length() - 1))) {
            return false;
        }

        boolean lastWasOperator = true;
        int parenthesesCount = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            // Allow '-' at the beginning as part of a negative number
            if (i == 0 && c == '-') {
                lastWasOperator = false;
                continue;
            }

            if (Character.isDigit(c)) {
                lastWasOperator = false;
            } else if (Character.isLetter(c)) {
                if (validRef(text.substring(i))) {
                    lastWasOperator = false;
                    while (i + 1 < text.length() && !containsChar(op3, text.charAt(i + 1))) {
                        i++;
                    }
                } else {
                    return false;
                }
            } else if (containsChar(op2, c)) {
                if (lastWasOperator) {
                    return false;
                }
                lastWasOperator = true;
            } else if (c == '(') {
                parenthesesCount++;
                if (text.charAt(i+1)== '-'){
                    i++;
                    continue;
                }
                lastWasOperator = true;
            } else if (c == ')') {
                parenthesesCount--;
                if (parenthesesCount < 0) {
                    return false;
                }
                lastWasOperator = false;
            } else if (c == '.') {
                if (!Character.isDigit(text.charAt(i + 1)) || !Character.isDigit(text.charAt(i - 1))) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (parenthesesCount != 0) {
            return false;
        }

        return true;
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
        if (s == null){
            return false;
        }
        // If no letters are found, it cannot be a valid cell reference.
        if (!s.matches(".*[a-zA-Z].*")) {
            return false;
        }
        // Iterate through each character in the string, check if the character is a letter,
        // if the letter is the second to last or third to last character Get the next character,
        // Return true if it's a digit.
        for (int i = 0; i < s.length(); i++) {
            char x = s.charAt(i);
            if (i==s.length()-1){
                return false;
            }
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
        if (isForm(s)) {
            return 3;
        }
        if (isNumber(s)) {
            return 2;
        }
        if (isText(s)) {
            return 1;
        }
        return -2;
    }


    private static boolean validRef(String s) {
        char[] c2 = {'+', '-', '*', '/', ')'}; // Array of invalid characters for cell references

        // If the string length is only 1, it's not a valid reference
        if (s.length() == 1) {
            return false;
        }

        // If the string length is 2, the second character must be a digit
        if (s.length() == 2) {
            char y = s.charAt(1);
            if (!Character.isDigit(y)) {
                return false;
            }
        }

        // If the string length is 3, the second must be a digit, and the third must be a digit or ')'
        if (s.length() == 3) {
            char y = s.charAt(1);
            char z = s.charAt(2);
            if (!Character.isDigit(y) || (!Character.isDigit(z) && z != ')')) {
                return false;
            }
        }

        // If the reference is longer than 3 characters, check additional rules
        if (s.length() > 3) {
            char y = s.charAt(1); // Second character
            char z = s.charAt(2); // Third character
            char a = s.charAt(3); // Fourth character

            // If the second character is not a digit, return false
            if (!Character.isDigit(y)) {
                return false;
            }

            // If the third character is not a digit, check if it is an allowed operator or bracket
            if (!Character.isDigit(z)) {
                int count = 0;

                // Check if `z` is in the array `c2`, which contains invalid reference characters
                if (!containsChar(c2, z)) {
                    return false;
                }

                // If both third and fourth characters are digits, it exceeds the valid index limit (max 99)
                if (Character.isDigit(z) && Character.isDigit(a)) {
                    return false;
                }
            }
        }

        // If all checks passed, it's a valid reference
        return true;
    }

}
