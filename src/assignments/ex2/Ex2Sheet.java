package assignments.ex2;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
// Add your documentation below:

public class Ex2Sheet implements Sheet {
    private Cell[][] table;
    // Add your code here

    // ///////////////////
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for(int i=0;i<x;i=i+1) {
            for(int j=0;j<y;j=j+1) {
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
        Cell c = get(x,y);
        if(c!=null) {ans = c.getData();}
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        if (cords != null){
            int x = CellEntry.letterToNumber(cords.charAt(0));
            int y = Integer.parseInt(cords.substring(1));
            ans = get(x,y);
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
        for (int i=0; i<width(); i++) {
            for (int j = 0; j < height(); j++) {
               set(i,j , eval(i, j));
            }
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx>=0 && yy>=0;
        if (width() <= xx || height() <=yy)
            ans = false;
        return ans;
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        for (int i=0; i<width(); i++){
            for (int j=0; j<height(); j++){
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
                if ( data.length < 3)
                    continue;

                try {
                  int a = Integer.parseInt(data[0]);
                  int b = Integer.parseInt(data[1]);
                  String c = data[2];
                  if (isIn(a,b))
                      set(a,b,c);
                } catch (NumberFormatException e) {
                   continue;
                }
            }
        }

    }

    @Override
    public void save(String fileName) throws IOException {
        // Add your code here

        /////////////////////
    }

    @Override
    public String eval(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;
        Cell c = get(x,y);
        if(c!=null && c.toString() != "") {
            if (c.toString().matches("-?\\d+(\\.\\d+)?")){
                ans = String.valueOf(get(x,y).toString());
                c.setType(2);
                return ans;
            }
           double value = computeForm(get(x, y).toString());
            ans = String.valueOf(value);
            c.setType(3);
        }
        return ans;
        }

        public void clearCells() {
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    set(i, j, "");
                }
            }
        }

    public Double computeForm(String form) {
        form = form.substring(1);
        if (SCell.isNumber(form)) {
            String ans = form.replaceAll("[\\(\\)]", "");
            return Double.parseDouble(ans);
        }
        int indexf = 0,indexp = 0,counter = 0;
        String part = form;
        ArrayList<Double> num = new ArrayList<>();
        ArrayList<Character> operator = new ArrayList<>();
        LinkedList<String> list = new LinkedList<>();


        while (indexf < form.length()-1) {
            if (Character.isLetter(part.charAt(0))) {
                indexp = part(part) + 1;
                indexf = indexf + indexp;
                part = part.substring(0, indexp);
                num.add(mainOP(part));
                if (indexf != form.length() - 1) {
                    if (Character.isDigit(form.charAt(indexf)))
                        indexf++;
                    operator.add(form.charAt(indexf));
                    part = form.substring(indexf + 1);
                }
            }
            if (part.charAt(0)!= '(') {
                indexp = part(part) + 1;
                indexf = indexf + indexp;
                part = part.substring(0, indexp);
                num.add(mainOP(part));
                if (indexf != form.length() - 1) {
                    if (Character.isDigit(form.charAt(indexf)))
                        indexf++;
                    operator.add(form.charAt(indexf));
                    part = form.substring(indexf + 1);
                }
            }
            if (part.charAt(0) == '(') {
                indexp = part(part);
                indexf = indexf + indexp;
                part = part.substring(0, indexp+1);
                num.add(mainOP(part));
                if (indexf != form.length() - 1) {
                    while (Character.isDigit(form.charAt(indexf)) || form.charAt(indexf) == ')')
                        indexf++;
                    operator.add(form.charAt(indexf));
                    part = form.substring(indexf + 1);
                }
            }}

        if (num.size() == 1){
            return num.get(0);
        }
        for (int i=0; i<operator.size();i++){
            if (operator.get(i) == '*' || operator.get(i) == '/'){
                char op = operator.get(i);
                if (op == '*')
                    num.set(i,num.get(i) * num.get(i+1));
                if (op == '/')
                    num.set(i,num.get(i) / num.get(i+1));
                num.remove(i+1);
                operator.remove(i);
            }
        }
        int j =0;
        while (num.size() > 1){
            char op = operator.get(j);
            if (op == '+')
                num.set(j,num.get(j) + num.get(j+1));
            if (op == '-')
                num.set(j,num.get(j) - num.get(j+1));
            num.remove(j+1);
            operator.remove(j);
        }
        return num.get(0);
    }

    private Double mainOP(String s){
        if (s == null || s.isEmpty()) {
            return 0.0;
        }
            int sum = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '(')
                    sum++;
                if (s.charAt(i) == ')')
                    sum--;
                if (sum == 0 && (s.charAt(i) == '/' || s.charAt(i) == '*')) {
                    Double p1 = mainOP(s.substring(0, i));
                    Double p2 = mainOP(s.substring(i + 1));
                    char op = s.charAt(i);
                    if (op == '*') {
                        return p1 * p2;
                    }
                    if (op == '/')
                        return p1 / p2;

                }
            }


            sum = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '(')
                    sum++;
                if (s.charAt(i) == ')')
                    sum--;
                if (sum == 0 && (s.charAt(i) == '-' || s.charAt(i) == '+')) {
                    Double p3 = mainOP(s.substring(0, i));
                    Double p4 = mainOP(s.substring(i + 1));
                    char op = s.charAt(i);
                    if (op == '+') {
                        return p3 + p4;
                    }
                    if (op == '-')
                        return p3 - p4;
                }
            }

            sum = 0;
            int first = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '(') {
                    sum++;
                    if (sum == 1)
                        first = i;
                }
                if (s.charAt(i) == ')') {
                    sum--;
                    if (sum == 0) {
                        double p5 = mainOP(s.substring(first + 1, i));
                        return p5;
                    }

                }
            }
        if (!s.isEmpty() && Character.isLetter(s.charAt(0))) {
           Cell ref = get(s);
            double p6 = mainOP(ref.getData());
        }


        return Double.parseDouble(s);
    }

    public int part (String s) {
        int sum = 1;
        if (s.charAt(0) == '(') {
            for (int i = 1; i < s.length(); i++) {
                if (s.charAt(i) == '(') {
                    sum++;
                }
                if (s.charAt(i) == ')') {
                    sum--;
                }
                if (sum == 0){
                    return i;
                }
            }
        }

        if(Character.isDigit((s.charAt(0))) || s.charAt(0) == '-'){
            for (int i =0; i<s.length();i++){
                if (!Character.isDigit((s.charAt(i))) && s.charAt(i) != '.')
                    return i -1;
            }
            return s.length()-1;
        }

        if (Character.isLetter((s.charAt(0)))){
            for (int i = 1; i < s.length(); i++) {
                if (!Character.isDigit((s.charAt(i)))){
                    return i-1;
                }
            }
        }
        return -1;
    }
}
