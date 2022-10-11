package ir.ghandchi.interview;

import java.util.Random;
import java.util.Stack;
import java.util.stream.IntStream;

/**
 * Date & Time: 2022-10-10 20:59
 * This class has been developed for evaluating candidate (the author) in employment process.
 *
 * @author <a href="mailto:askar.ghandchi@gmail.com">Askar Ghandchi</a>
 * @version 1.0
 */
public class Expression {
    private static final char BLANK = ' ';
    private static final char OPENING_PARENTHESES = '(';
    private static final char CLOSING_PARENTHESES = ')';
    private static final char[] OPERANDS = {'+', '-', '*', '/'};
    private String expressionWithVariables;
    private String expressionWithValues;

    private Expression(int level, int[] values) {
        if (level < 1 || level > 26)
            throw new RuntimeException("Invalid level number; the level range is '1 <= level <= 26'.");
        if (values == null || values.length == 0)
            throw new RuntimeException("values can not be null or empty.");
        if (level != values.length)
            throw new RuntimeException("The number of variables and values should be equal.");

        expressionWithVariables = generateExpression(level);
        expressionWithVariables = optimizeExpression(expressionWithVariables);
        setValues(values);
    }

    /**
     * For unit test purposes
     *
     * @param expressionWithValues ready expression with values
     */
    private Expression(String expressionWithValues) {
        validate(expressionWithValues);
        this.expressionWithValues = expressionWithValues;
    }

    private void validate(String expressionWithValues) {
        if (expressionWithValues == null || expressionWithValues.length() == 0)
            throw new RuntimeException("Invalid expression.");
        // TODO: 10/11/22 should be completed
    }

    public String getExpressionWithValues() {
        return expressionWithValues;
    }

    private String generateExpression(int level) {
        char[] operands = generateOperands(level);
        StringBuilder expression = new StringBuilder();
        Random random = new Random();
        int i = 0;
        boolean insideParenthesis = false;
        for (; i < level - 1; i++) {
            if (insideParenthesis) {
                if (random.nextBoolean()) {
                    expression.append(operands[i]).append(CLOSING_PARENTHESES).append(getAnOperator());
                    insideParenthesis = false;
                } else {
                    expression.append(operands[i]).append(getAnOperator());
                }
            } else {
                if (level - i > 1)
                    insideParenthesis = random.nextBoolean();
                if (insideParenthesis)
                    expression.append(OPENING_PARENTHESES).append(operands[i]).append(getAnOperator());
                else {
                    expression.append(operands[i]).append(getAnOperator());
                }
            }
        }

        expression.append(operands[i]);

        if (insideParenthesis)
            expression.append(CLOSING_PARENTHESES);

        return expression.toString();
    }

    /**
     * For now, this method should be completed
     *
     * @param expression unoptimized expression
     * @return optimized expression
     */
    private String optimizeExpression(String expression) {
        if (expression.lastIndexOf(OPENING_PARENTHESES) == 0 && expression.lastIndexOf(CLOSING_PARENTHESES) == expression.length() - 1)
            return expression.substring(1, expression.length() - 1);
        // TODO: 10/10/22 should be completed
        return expression;
    }

    private char[] generateOperands(int level) {
        char[] operands = new char[level];
        for (int i = 0; i < level; i++) {
            operands[i] = (char) (i + 97);
        }
        return operands;
    }

    private char getAnOperator() {
        return OPERANDS[(int) (OPERANDS.length * Math.random())];
    }

    public void setValues(int[] values) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char ch : expressionWithVariables.toCharArray()) {
            if (ch == OPENING_PARENTHESES)
                stringBuilder.append(BLANK).append(ch);
            else if (ch == CLOSING_PARENTHESES)
                stringBuilder.append(ch).append(BLANK);
            else if (isOperator(ch))
                stringBuilder.append(ch);
            else
                stringBuilder.append(BLANK).append(values[ch - 97]).append(BLANK);
        }

        expressionWithValues = stringBuilder.toString().trim();
    }

    private boolean isOperator(char ch) {
        return String.valueOf(OPERANDS).indexOf(ch) != -1;
    }

    public long evaluate() {
        char[] chars = expressionWithValues.toCharArray();
        Stack<Double> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == BLANK)
                continue;

            if (Character.isDigit(chars[i])) {
                StringBuilder number = new StringBuilder();

                while (i < chars.length && Character.isDigit(chars[i])) // for more than one digit
                    number.append(chars[i++]);
                operands.push((double) Integer.parseInt(number.toString()));

                i--; // correcting the offset
            } else if (chars[i] == OPENING_PARENTHESES) {
                operators.push(chars[i]);
            } else if (chars[i] == CLOSING_PARENTHESES) {
                while (operators.peek() != OPENING_PARENTHESES) {
                    operands.push(calculate(operators.pop(), operands.pop(), operands.pop()));
                }
                operators.pop();
            } else if (isOperator(chars[i])) {
                while (!operators.empty() && hasPriority(chars[i], operators.peek()))
                    operands.push(calculate(operators.pop(), operands.pop(), operands.pop()));

                operators.push(chars[i]);
            }
        }

        while (!operators.empty()) // remaining values
            operands.push(calculate(operators.pop(), operands.pop(), operands.pop()));

        return operands.pop().longValue();
    }

    private boolean hasPriority(char operator1, char operator2) {
        if (operator2 == OPENING_PARENTHESES || operator2 == CLOSING_PARENTHESES)
            return false;
        if ((operator1 == '*' || operator1 == '/') && (operator2 == '+' || operator2 == '-'))
            return false;
        else
            return true;
    }

    private double calculate(char operator, double operand2, double operand1) {
        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                if (operand2 == 0)
                    throw new RuntimeException("Division by zero.");
                return operand1 / operand2;
            default:
                throw new RuntimeException("Unknown operator '" + operator + "'.");
        }
    }

    public static ExpressionLevel getBuilder() {
        return new Expression.Builder();
    }

    interface ExpressionLevel {
        ExpressionWithValues level(int level);

        ExpressionCreator expression(String expressionWithValues);
    }

    interface ExpressionWithValues {
        ExpressionCreator withValues(IntStream values);

        ExpressionCreator withValues(int[] values);
    }

    interface ExpressionCreator {
        Expression build();
    }

    private static class Builder implements ExpressionCreator, ExpressionLevel, ExpressionWithValues {
        private int level;
        private int[] values;
        private String expressionWithValues;

        @Override
        public ExpressionWithValues level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public ExpressionCreator expression(String expressionWithValues) {
            this.expressionWithValues = expressionWithValues;
            return this;
        }

        @Override
        public ExpressionCreator withValues(IntStream values) {
            return withValues(values.toArray());
        }

        @Override
        public ExpressionCreator withValues(int[] values) {
            this.values = values;
            return this;
        }

        @Override
        public Expression build() {
            return expressionWithValues != null ? new Expression(expressionWithValues) : new Expression(level, values);
        }
    }
}
