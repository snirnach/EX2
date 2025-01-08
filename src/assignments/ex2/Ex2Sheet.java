
package assignments.ex2;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static assignments.ex2.SCell.*;

//Represents a spreadsheet with cells that can contain values or formulas.
public class Ex2Sheet implements Sheet {
    private Cell[][] table;

    // Constructor: initializes the table with empty cells.
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for (int i = 0; i < x; i = i + 1) {
            for (int j = 0; j < y; j = j + 1) {
                table[i][j] = new SCell("");
            }
        }
        eval();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    // Returns the evaluated value of the cell at (x, y).
    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;// Default value if the cell is empty.
        Cell c = get(x, y);
        if (c != null) {
            ans = eval(x,y);// Compute the value of the cell.
            if (ans== Ex2Utils.ERR_CYCLE){ // Check if a cycle is detected.
                table[x][y].setType(-1);//change type
            }
        }
        return ans;
    }

    // Returns the Cell object at position (x, y).
    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }
    // Returns a Cell object given its string reference (e.g., "A1").
    @Override
    public Cell get(String cords) {
        // Retrieves a cell based on its coordinate string (e.g., "A1").
        Cell ans = null;
        if (cords != null) {
            // Convert the column letter to a numeric index
            int x = CellEntry.letterToNumber(cords.charAt(0));

            // Extract the row number from the string
            int y = Integer.parseInt(cords.substring(1));

            // Retrieve the cell at the computed coordinates
            ans = get(x, y);
        }
        return ans;
    }

    @Override
    public int width() {
        // Returns the number of columns in the sheet
        return table.length;
    }

    @Override
    public int height() {
        // Returns the number of rows in the sheet
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String s) {
        // Assigns a new value to the cell at (x, y)
        Cell c = new SCell(s);
        table[x][y] = c;
    }

    @Override
    public void eval() {
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                    eval(i, j); // evaluate the cell
                table[i][j].setOrder(computeOrder(table[i][j].getData(),i,j,new HashSet<>()));
                }
            }
        }


    @Override
    public boolean isIn(int xx, int yy) {
        // Checks if the given coordinates are within the bounds of the sheet
        boolean ans = xx >= 0 && yy >= 0;
        if (width() <= xx || height() <= yy)
            ans = false;
        return ans;
    }

    @Override
    public int[][] depth() {
        // Computes the dependency depth of each cell in the sheet
        int[][] ans = new int[width()][height()];

        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                // Compute the order (depth) of dependencies for each cell
                int depth = computeOrder(table[i][j].getData(), i, j, new HashSet<>());
                ans[i][j] = depth;
            }
        }
        return ans;
    }


    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
            clearCells(); // Clears all existing cell values before loading new ones.

            String line;
            boolean isFirstLine = true; // A flag to skip the first line in the file.

            // Read the file line by line
            while ((line = file.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip the first line (header or metadata)
                    continue;
                }

                // Split the line into parts using commas (CSV format)
                String[] data = line.split(",", 3);
                if (data.length < 3) // Ensure we have at least 3 values (x, y, data)
                    continue;

                try {
                    int a = Integer.parseInt(data[0]); // Convert the first value to x-coordinate
                    int b = Integer.parseInt(data[1]); // Convert the second value to y-coordinate
                    String c = data[2]; // Extract the cell value

                    // Check if coordinates are within the sheet bounds before setting the value
                    if (isIn(a, b))
                        set(a, b, c); // Assign the value to the respective cell
                } catch (NumberFormatException e) {
                    continue; // Ignore lines with invalid coordinates
                }
            }
        }
    }

    /**
     * Saves the current spreadsheet to a file in CSV format.
     */
    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("this line should be ignored\n"); // Write an initial ignored line

            // Iterate over all cells in the sheet
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    String value = table[i][j].getData(); // Get the data of the cell

                    // Only save non-empty cells
                    if (!value.equals(Ex2Utils.EMPTY_CELL)) {
                        writer.write(i + "," + j + "," + value + "\n"); // Write to file
                    }
                }
            }
        }
    }

    @Override
    public String eval(int x, int y) {
        // Retrieve the cell at the given coordinates.
        Cell c = get(x, y);

        // If the cell is null, has no data, or is empty, return the default empty cell value.
        if (c == null || c.getData() == null || c.getData().isEmpty()) {
            return Ex2Utils.EMPTY_CELL;
        }

        // Get the actual content of the cell.
        String data = c.getData();
        data = data.replaceAll("\\s+", "").toUpperCase();

        // Check for cyclic dependencies; if a cycle is detected, mark the cell and return an error.
        if (computeOrder(data, x, y, new HashSet<>()) == -1) {
            table[x][y].setType(-1); // Set the cell type to indicate an error.
            return Ex2Utils.ERR_CYCLE; // Return cycle error message.
        }

        // If the cell contains a formula, compute its value.
        if (isForm(data)) {
            double result = computeForm(data);

            // If the computation resulted in a cycle detection, return the cycle error.
            if (result == Double.NEGATIVE_INFINITY) {
                return Ex2Utils.ERR_CYCLE;
            }

            // If the result is a valid number, update the cell type and return the computed value.
            if (!Double.isInfinite(result)) {
                table[x][y].setType(3); // Type 3 indicates a computed formula.
                return String.valueOf(result);
            }
        }

        // If the cell contains a valid number, return it as a string.
        if (SCell.isNumber(data)) {
            Double data1 = Double.parseDouble(data);
            table[x][y].setType(2); // Type 2 indicates a numeric value.
            return String.valueOf(data1);
        }

        // If the cell contains a number enclosed in parentheses, remove them and return the number.
        if (data.charAt(0) == '(' && data.charAt(data.length() - 1) == ')'
                && SCell.isNumber(data.substring(1, data.length() - 1))) {
            Double data1 = Double.parseDouble(data.substring(1, data.length() - 1));
            table[x][y].setType(2); // Type 2 indicates a numeric value.
            return String.valueOf(data1);
        }

        // If the cell contains text (not a formula), return it as is.
        if (isText(data)) {
            table[x][y].setType(1); // Type 1 indicates a text value.
            return data;
        }

        // If none of the conditions were met, mark the cell as an invalid formula and return an error.
        table[x][y].setType(-2); // Type -2 indicates a formula error.
        return Ex2Utils.ERR_FORM;
    }

    /**
     * Clears all cells in the spreadsheet by resetting their values to empty strings.
     */
    public void clearCells() {
        // Iterate through all cells in the spreadsheet.
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                set(i, j, ""); // Reset each cell to an empty string.
            }
        }
    }


    public Double computeForm(String form) {
        // If the formula is null or empty, return positive infinity (error indicator).
        if (form == null || form.isEmpty()) {
            return Double.POSITIVE_INFINITY;
        }

        // If the formula starts with '=', remove it for processing.
        if (form.charAt(0) == '=') {
            form = form.substring(1);
        }

        // If the formula is a simple number, return its value as a double.
        if (SCell.isNumber(form)) {
            return Double.parseDouble(form);
        }

        // Lists to store numbers and operators separately.
        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<Character> operators = new ArrayList<>();

        // Remove all whitespace and convert formula to uppercase for consistency.
        form = form.replaceAll("\\s+", "").toUpperCase();

        // Split the formula based on mathematical operators while keeping parentheses intact.
        String[] parts = form.split("((?=[-+*/])|(?<=[-+*/]))(?![^()]*\\))");
        // String[] parts = form.split("(?<=[-+*/()])|(?=[-+*/()])");

        // Iterate over each extracted part.
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (part.equals("-") && (i == 0 || operators.contains(parts[i - 1].charAt(0)) || parts[i - 1].equals("("))) {
                parts[i + 1] = "-" + parts[i + 1];
                continue;
            }
            // If the part is a number, add it to the numbers list.
            if (SCell.isNumber(part)) {
                numbers.add(Double.parseDouble(part));
            }

            // If the part is an expression within parentheses, evaluate it recursively.
          if (part.charAt(0) == '(') {
                    numbers.add(computeForm(part.substring(1, part.length() - 1)));
            }

            // If the part is a cell reference (e.g., A1, B2), retrieve its value.
            else if (SCell.CellReference(part)) {
                int refX = CellEntry.letterToNumber(part.charAt(0)); // Convert letter to column index.
                int refY = Integer.parseInt(part.substring(1)); // Convert number to row index.

                SCell sCell = (SCell) get(part); // Retrieve the referenced cell.
                String refline = sCell.getData(); // Get the raw data from the referenced cell.

                // Check for cyclic dependencies; return negative infinity if detected.
                if (computeOrder(refline, refX, refY, new HashSet<>()) == -1) {
                    return Double.NEGATIVE_INFINITY;
                }

                // Evaluate the referenced cell and parse it if it's a number.
                String refValue = eval(refX, refY);
                if (SCell.isNumber(refValue)) {
                    numbers.add(Double.parseDouble(refValue));
                } else {
                    return Double.POSITIVE_INFINITY; // If reference is invalid, return an error indicator.
                }
            }

            // If the part is a mathematical operator, add it to the operators list.
            if (part.length() == 1 && isOperator(part.charAt(0))) {
                operators.add(part.charAt(0));
            }
        }

        // Process multiplication and division first (operator precedence).
        for (int i = 0; i < operators.size(); i++) {
            char op = operators.get(i);
            if (op == '*') {
                double result = numbers.get(i) * numbers.get(i + 1);
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--; // Adjust index after modification.
            }
            if (op == '/') {
                double result = numbers.get(i) / numbers.get(i + 1);
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--; // Adjust index after modification.
            }
        }

        // Process addition and subtraction.
        for (int i = 0; i < operators.size(); i++) {
            char op = operators.get(i);
            if (op == '+') {
                double result = numbers.get(i) + numbers.get(i + 1);
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--; // Adjust index after modification.
            }
            if (op == '-') {
                double result = numbers.get(i) - numbers.get(i + 1);
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--; // Adjust index after modification.
            }
        }

        // Try to return the final computed value, handling any exceptions.
        try {
            return numbers.get(0);
        } catch (Exception e) {
            return Double.POSITIVE_INFINITY; // Return error indicator if computation fails.
        }
    }


    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }


//function return the order of Cell
public int computeOrder(String s, int x, int y, Set<String> visited) {
        // If the cell is empty or contains a number/text, there is no dependency to calculate.
        if (s == null || s.isEmpty() || isNumber(s) || isText(s)) {
            return 0;
        }

        // If the cell contains a formula, remove the '=' at the beginning.
        if (s.charAt(0) == '=') {
            s = s.substring(1);
        }

        // Create a unique identifier for the cell to check for cycles.
        String cellRef = (char) ('A' + x) + String.valueOf(y);

        // If the cell reference already exists in the visited set, a cycle is detected.
        if (visited.contains(cellRef)) {
            return -1;
        }

        // Add the current cell to the visited set to track dependencies.
        visited.add(cellRef);

        // Retrieve the list of dependent cells from the formula.
        ArrayList<String> dependencies = Dependencies(s);

        int maxOrder = 0; // Default: the maximum dependency order is 0.

        // Iterate through all cells that the formula depends on.
        for (String dep : dependencies) {
            dep.replaceAll("\\s+", "").toUpperCase();
            int depX = CellEntry.letterToNumber(dep.charAt(0)); // Convert column letter to number.
            int depY = Integer.parseInt(dep.substring(1)); // Extract row number.

            // Check if the referenced cell is within the spreadsheet bounds.
            if (!isIn(depX, depY)) {
                continue;
            }

            // Retrieve the dependent cell.
            SCell dependentCell = (SCell) get(depX, depY);

            // Recursively compute the order of the dependent cell.
            int depOrder = computeOrder(dependentCell.getData(), depX, depY, visited);

            // If a cycle is detected in one of the dependencies, return -1.
            if (depOrder == -1) {
                return -1;
            }

            // Update the maximum order based on the dependency.
            maxOrder = Math.max(maxOrder, depOrder);
        }

        // Remove the current cell from the visited set after processing its dependencies.
        visited.remove(cellRef);

        // The order of this cell is 1 + the highest order among its dependencies.
        return maxOrder + 1;
    }

    //function return Arraylist with all cords in the formula
    private ArrayList<String> Dependencies(String form) {
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

}


