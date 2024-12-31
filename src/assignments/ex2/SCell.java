package assignments.ex2;
// Add your documentation below:

public class SCell implements Cell {
    private String line;
    private int type;


    public SCell(String s) {
        setData(s);
    }

    @Override
    public int getOrder() {
        if (isNumber(line) || isText(line)) {
            return 0;
        }


        return -1;
    }

    //@Override
    @Override
    public String toString() {
        return getData();
    }

    @Override
public void setData(String s) {
        line = s;
    }
    @Override
    public String getData() {
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public void setOrder(int t) {
        // Add your code here

    }

    // check if the text are number.
    public boolean isNumber(String text) {
        boolean ans;
        try {
            Double.parseDouble(text);
            ans = true;
        } catch (Exception e) {
            ans = false;
        }

        return ans;
    }

    public boolean isText(String text) {
        boolean ans = true;
        if (isNumber(text))
            ans = false;
        if (isForm(text))
            ans = false;

        return ans;
    }

    public boolean isForm(String text1) {
        if (text1.charAt(0) != '=') // must be '=' in the first char
            return false;

        if (isNumber(text1.substring(1, text1.length()))) // if the formula only number, then it is valid.
            return true;

        if (text1.charAt(1) == '(' && lastChar(text1) == ')' && text1.length() > 3)//If the formula contains only parentheses and a number, then it is valid.
            if (isNumber(text1.substring(2, text1.length() - 1))) {
                return true;
            }
        if (goodFormula(text1.substring(1, text1.length())))
            return true;
        return false;
    }

    private boolean goodFormula(String s){
        int sum;
        char [] c = {'+','-','*','/','(',')','.'};
        char [] c1 = {'+','-','*','/','(','.'};
        char [] c2 = {'+','-','*','/',')','.'};
        char [] c3 = {'+','-','*','/'};
        for (int i=0; i<c1.length;i++){
            if (lastChar(s) == c1[i] || s.charAt(0) == c2[i]) //If there is an operator from the array `c1` at the end, then it is invalid.
                return false;
        }
        //Iterate over all the characters and check if a character is neither a digit nor an operator from `c`.
        // If such a character is found, the formula is invalid.
        for (int i=0; i< s.length();i++){
            sum = 0;
            for (int j=0;j<c.length;j++) {
                if (s.charAt(i) == c[j])
                    sum = 1;
            }
            if (!Character.isDigit(s.charAt(i)) && sum == 0)
                return false;

        }
        //If there is a mathematical operator and the character before or after it is invalid, then the formula is invalid.
        for (int i =1; i< s.length()-1;i++){
            for (int j =0; j<c3.length;j++){
                if (s.charAt(i) == c3[j] && ((!Character.isDigit(s.charAt(i-1)) && s.charAt(i-1) != ')') || (!Character.isDigit(s.charAt(i+1)) && s.charAt(i+1) != '(')))
                    return false;
            }
        }

        //If there are parentheses with nothing between them, or if the number of opening parentheses does not match the number of closing parentheses,
        // then the formula is invalid.
        sum =0;
        for (int i =0; i<s.length();i++) {
            if (s.charAt(i) == '(') {
                sum++;
                if (s.charAt(i + 1) == ')')
                    return false;
            }
            if (s.charAt(i) == ')')
                sum--;
        }
        if (sum != 0)
            return false;

        for (int i = 0; i<s.length();i++){
            char x = s.charAt(i);
            if (Character.isLetter(x)) {
                if (i + 1 == s.length() - 1) {
                    char y = s.charAt(i + 1);
                    if (!Character.isDigit(y)) {
                        return false;
                    }
                }
                if ( i + 2 == s.length() - 1){
                    char y = s.charAt(i + 1);
                    char z = s.charAt(i + 2);
                    if (!Character.isDigit(y) || (!Character.isDigit(z) && z != ')')) {
                        return false;
                    }
                }
                if (i + 2 < s.length() - 1) {
                    char y = s.charAt(i + 1);
                    char z = s.charAt(i + 2);
                    char a = s.charAt(i + 3);
                    if (!Character.isDigit(y)) {
                        return false;
                    }
                    if (!Character.isDigit(z)) {
                        int count = 0;
                        for (int j = 0; j < c2.length - 1; j++) {
                            if (c2[j] == z ){
                                count++;
                            }
                        }
                        if (count == 0){return false;}
                    }
                    if (Character.isDigit(a)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // return the last char is the string
    private char lastChar (String s){
        return s.charAt(s.length()-1);
    }

    public static boolean CellReference(String s) {
        if (!s.matches(".*[a-zA-Z].*")){
            return false;
        }
        for (int i = 0; i<s.length();i++){
           char x = s.charAt(i);
           if (Character.isLetter(x)) {
               if (i + 1 == s.length() - 1 || i + 2 == s.length() - 1) {
                   char y = s.charAt(i + 1);
                   return Character.isDigit(y);
               }
               if (i + 2 < s.length() - 1) {
                   char y = s.charAt(i + 1);
                   char z = s.charAt(i + 2);
                   char a = s.charAt(i + 3);
                   if (!Character.isDigit(y)) {
                       return false;
                   }
                   if (Character.isDigit(y) && Character.isDigit(z) && Character.isDigit(a)) {
                       return false;
                   }
               }
           }

        }
       return true;
    }
}
