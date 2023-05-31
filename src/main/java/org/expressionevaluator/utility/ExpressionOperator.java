package org.expressionevaluator.utility;

public enum ExpressionOperator {
    GREATER_THAN(1, ">",  ">"),
    GREATER_OR_EQUALS_THAN(2, ">=", ">="),
    LESSER_THAN(3, "<", "<"),
    LESSER_OR_EQUALS_THAN(4, "<=", "<="),
    EQUALS(5, "==", "=="),
    NOT_EQUALS(6, "!=", "!="),
    AND(7, "&&", "&&"),
    OR(8, "||", "\\|\\|");


    public static ExpressionOperator findBySymbol(String symbol) {
        return switch (symbol) {
            case ">" -> ExpressionOperator.GREATER_THAN;
            case "<" -> ExpressionOperator.LESSER_THAN;
            case "<=" -> ExpressionOperator.LESSER_OR_EQUALS_THAN;
            case ">=" -> ExpressionOperator.GREATER_OR_EQUALS_THAN;
            case "==" -> ExpressionOperator.EQUALS;
            case "!=" -> ExpressionOperator.NOT_EQUALS;
            case "&&" -> ExpressionOperator.AND;
            case "||" -> ExpressionOperator.OR;
            default -> throw new IllegalArgumentException("Expression operator not found: " + symbol);
        };

    }

    public static boolean isLogicOperator(String symbol) {
        return switch (symbol) {
            case ">", "<", "<=", ">=", "==", "!=", "&&", "||" -> true;
            default -> false;
        };
    }


    private final int precedenceOrder;
    private final String symbol;
    private final String splittingSymbol;



    ExpressionOperator(int precedenceOrder, String symbol, String splittingSymbol) {
        this.precedenceOrder = precedenceOrder;
        this.symbol = symbol;
        this.splittingSymbol = splittingSymbol;
    }

    public int getPrecedenceOrder() {
        return precedenceOrder;
    }

    public String getSymbol() {
        return symbol;
    }
    public String getSplittingSymbol() {
        return splittingSymbol;
    }
}
