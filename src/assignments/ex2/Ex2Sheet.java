
package assignments.ex2;
import java.io.*;
import java.util.ArrayList;

public class Ex2Sheet implements Sheet {
    private Cell[][] table;

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

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;
        Cell c = get(x, y);
        if (c != null) {
            ans = c.getData();
        }
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        if (cords != null) {
            int x = CellEntry.letterToNumber(cords.charAt(0));
            int y = Integer.parseInt(cords.substring(1));
            ans = get(x, y);
        }
        return ans;
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String s) {
        Cell c = new SCell(s);
        table[x][y] = c;
    }

    @Override
    public void eval() {
        int[][] dd = depth();
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                set(i, j, eval(i, j));
            }
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx >= 0 && yy >= 0;
        if (width() <= xx || height() <= yy)
            ans = false;
        return ans;
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                int depth = table[i][j].getOrder();
                ans[i][j] = depth;
            }
        }
        return ans;
    }

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
            clearCells();

            String line;
            boolean isFirstLine = true;
            while ((line = file.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",", 3);
                if (data.length < 3)
                    continue;

                try {
                    int a = Integer.parseInt(data[0]);
                    int b = Integer.parseInt(data[1]);
                    String c = data[2];
                    if (isIn(a, b))
                        set(a, b, c);
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }

    }

    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment - this line should be ignored\n");
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    String value = table[i][j].getData();
                    if (!value.equals(Ex2Utils.EMPTY_CELL)) {
                        writer.write(i + "," + j + "," + value + "\n");
                    }
                }
            }
        }
    }
    @Override
    public String eval(int x, int y) {
        Cell c = get(x, y);
        if (c == null || c.getData() == null || c.getData().isEmpty()) {
            return Ex2Utils.EMPTY_CELL;
        }

        String data = c.getData();

        if (SCell.isNumber(data)) {
            c.setType(2);
            Double data1 = Double.parseDouble(data);
            return String.valueOf(data1);
        }
        if (data.charAt(0)=='(' && SCell.isNumber(data.substring(1, data.length()-1))){
            Double data1 = Double.parseDouble(data.substring(1, data.length()-1));
            c.setType(2);
            return String.valueOf(data1);
        }


        if (SCell.isText(data)) {
            c.setType(1);
            return data;
        }

        if (SCell.isForm(data)) {
            if (hasCycle(data, x, y)) {
                c.setType(-1);
                return "ERR_CYCLE_FORM";
            }

            double result = computeForm(data, x, y);
            if (!Double.isInfinite(result)) {
                c.setType(3);
                return String.valueOf(result);
            }
        }
        return "ERR_FORM_FORMAT";
        }




    public void clearCells() {
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                set(i, j, "");
            }
        }
    }

    public Double computeForm(String form, int x, int y) {
        if (form == null || form.isEmpty()){

            return Double.POSITIVE_INFINITY;
        }
                if (form.charAt(0) == '='){
            form = form.substring(1);
        }

        if (SCell.isNumber(form)) {
            return Double.parseDouble(form);
        }

        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<Character> operators = new ArrayList<>();

        form = form.replaceAll("\\s+", "");
        String[] parts = form.split("((?=[-+*/])|(?<=[-+*/]))(?![^()]*\\))");


        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            part = part.toUpperCase();

            if (SCell.isNumber(part)) {
                numbers.add(Double.parseDouble(part));
            }
            if (part.charAt(0) == '('){

                numbers.add(computeForm(part.substring(1, part.length()-1), x,y));
            }
            if (SCell.CellReference(part)) {
                int refX = CellEntry.letterToNumber(part.charAt(0));
                int refY = Integer.parseInt(part.substring(1));
                String refValue = eval(refX, refY);
                if (SCell.isNumber(refValue)) {
                    numbers.add(Double.parseDouble(refValue));
                }
            }
                if (part.length() == 1 && isOperator(part.charAt(0))) {
                    operators.add(part.charAt(0));
                }
            }

        for (int i = 0; i < operators.size(); i++) {
            char op = operators.get(i);
            if (op == '*'){
                double result = numbers.get(i) * numbers.get(i + 1);
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--; //
            }
                if (op == '/') {
                    double result = numbers.get(i) / numbers.get(i + 1);
                    numbers.set(i, result);
                    numbers.remove(i + 1);
                    operators.remove(i);
                    i--; //
            }
        }


        for (int i = 0; i < operators.size(); i++) {
            char op = operators.get(i);
            if (op == '+'){
                double result = numbers.get(i) + numbers.get(i + 1);
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--; //
            }
            if (op == '-') {
                double result = numbers.get(i) - numbers.get(i + 1);
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--; //
            }
        }

        return numbers.get(0);
    }
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    public static boolean isForm(String text) {
        if (text == null || text.isEmpty() || text.charAt(0) != '=') {
            return false;
        }

        String formula = text.substring(1);
        formula = formula.replaceAll("\\s+", "");

        // אם זו רק ספרה אחת
        if (SCell.isNumber(formula)) {
            return true;
        }

        // אם זה ביטוי מתמטי חוקי (מספרים, אותיות, אופרטורים)
        return formula.matches("[A-Za-z0-9+\\-*/().]+");
    }


    private boolean hasCycle(String s, int x, int y) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        String cellRef = (char) ('A' + x) + String.valueOf(y);
        s = s.toUpperCase(); // Ensure all formulas are treated as uppercase
        if (s.equals("=" + cellRef)) { // Direct self-reference
            return true;
        }

        if (s.charAt(0) == '=') {
            s = s.substring(1); // Remove '=' for easier processing
        }

        if (SCell.isNumber(s) || SCell.isText(s)) {
            return false; // A number or text cannot create a cycle
        }

        if (isForm(s)) {
            if (!SCell.CellReference(s)) {
                return false; // If not a valid reference, it's not a cycle
            }

            // Get dependent cell references
            ArrayList<String> dependencies = SCell.Dependencies(s);

            // Check if the current cell reference appears in dependencies
            for (int i = 0; i < dependencies.size(); i++) {
                if (dependencies.get(i).equals(cellRef)) {
                    return true; // Found a circular reference
                }
            }

            // Iterate over dependencies and check if any forms a cycle
            for (int i = 0; i < dependencies.size(); i++) {
                String dep = dependencies.get(i);
                int depX = CellEntry.letterToNumber(dep.charAt(0));
                int depY = Integer.parseInt(dep.substring(1));

                if (hasCycle(table[depX][depY].getData(), depX, depY)) {
                    return true; // If any dependency forms a cycle, return true
                }
            }
        }

        return false;
    }


    public static boolean hasDuplicates(ArrayList<String> dep) {
        for (int i = 0; i < dep.size(); i++) {
            for (int j = i + 1; j < dep.size(); j++) {
                if (dep.get(i).equals(dep.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }
}

