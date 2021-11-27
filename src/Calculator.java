public class Calculator {

    public static double calculate(String infix) {

        StringBuilder operand = new StringBuilder();
        MyStack<String> operands = new MyStack<>(infix.length());

        for (Character c : toPostfix(infix).toCharArray()) {

            if (c == ' ') {
                operands.push(operand.toString());
                operand = new StringBuilder();

            } else if (isDigit(c)) {
                operand.append(c);

            } else if (isOperator(c)) {

                operands.push(String.valueOf(
                        calculate(c, operands.pop(), operands.pop())
                ));
            }
        }

        return Double.parseDouble(operands.pop());
    }

    private static double calculate(char operator, String operand2, String operand1) {

        if (operand1.equals(".") || operand2.equals(".")) {
            throw new RuntimeException("error");
        }

        double result = 0,
                o1 = Double.parseDouble(operand1),
                o2 = Double.parseDouble(operand2);

        switch (operator) {
            case '+' -> result = o1 + o2;
            case '-' -> result = o1 - o2;
            case '*' -> result = o1 * o2;
            case '/' -> {
                if (o2 == 0)
                    throw new RuntimeException("error");
                result = o1 / o2;
            }
            case '^' -> result = Math.pow(o1, o2);
        }
//        System.out.println(o1 + " " + operator + " " + o2 + " = " + result);
        return result;
    }

    private static boolean isValid(String infix) {
        if (infix.isEmpty())
            return false;

        int len = infix.length();
        char f = infix.charAt(0);
        char l = infix.charAt(len - 1);
        MyStack<Character> stack = new MyStack<>(len);

        if (!isDigit(f) && f != '(' && f != '+' && f != '-' || !isDigit(l) && l != ')' || l == '.') {
            return false;
        }
        for (int i = 0; i < len; i++) {

            char c = infix.charAt(i);

            if (isOperator(c)) {
                if (i > 0 && isOperator(infix.charAt(i - 1)))
                    return false;

            } else if (c == '(') {
                stack.push(c);

                if (i < len - 1 && ")*/^".indexOf(infix.charAt(i + 1)) != -1)
                    return false;
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(')
                    return false;

                if (i > 0 && isOperator(infix.charAt(i - 1)))
                    return false;

            } else if (!isDigit(c)) {
                return false;
            }
        }

        return stack.isEmpty();
    }

    private static String toPostfix(String infix) {
        infix = infix.replaceAll(" ", "");

        if (!isValid(infix))
            throw new RuntimeException("error");

        infix = infix
                .replaceAll("\\)([\\d])", ")*$1")
                .replaceAll("([\\d])\\(", "$1*(")
                .replaceAll("\\)\\(", ")*(");

//        System.out.println(infix);

        int len = infix.length();
        StringBuilder result = new StringBuilder();
        StringBuilder operand = new StringBuilder();
        MyStack<Character> operators = new MyStack<>(len);

        for (int i = 0; i < len; i++) {

            char c = infix.charAt(i);

            // form operand by putting digits together
            if (isDigit(c)) {
                operand.append(c);

            } else {

                // add operand to result
                if (!operand.isEmpty()) {
                    result.append(operand).append(" ");
                    operand = new StringBuilder();
                }

                // arrange operators by priority
                if (isOperator(c)) {

                    while (!operators.isEmpty() && !precedence(c, operators.peek())) {
                        result.append(operators.pop());
                    }
                    operators.push(c);

                    // check - is neg not sub  or  + is not sum
                    if ((c == '-' || c == '+') && (i == 0 || infix.charAt(i - 1) == '(')) {
                        result.append("0 ");
                    }

                } else if (c == '(') {
                    operators.push(c);

                } else if (c == ')') {

                    while (!operators.isEmpty() && operators.peek() != '(')
                        result.append(operators.pop());

                    operators.pop();
                }
            }
        }

        if (!operand.isEmpty()) {
            result.append(operand).append(" ");
        }

        while (!operators.isEmpty()) {
            result.append(operators.pop());
        }

//        System.out.println(result);
        return result.toString();
    }

    private static boolean isOperator(Character c) {
        return "+-*/^".indexOf(c) != -1;
    }

    private static boolean isDigit(Character c) {
        return c == '.' || Character.isDigit(c);
    }

    // true if "a" precedence is greater than or equal to the precedence of "b"
    private static boolean precedence(Character a, Character b) {
        String oprs = "( +- */ ^";
        int diff = oprs.indexOf(a) - oprs.indexOf(b);
        return diff > 0 || diff == 0 && a == '^';
    }
}
