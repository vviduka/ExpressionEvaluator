package org.expressionevaluator.validation.expression;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.expressionevaluator.utility.ExpressionOperator;
import org.expressionevaluator.utility.tree.ExpressionTokenizer;

import java.util.List;

public class ExpressionValidator implements ConstraintValidator<ExpressionConstraint, String> {

    @Override
    public boolean isValid(String expression, ConstraintValidatorContext constraintValidatorContext) {
        boolean hasOperator = false;
        int parenthesesOpened = 0;
        List<String> tokens = ExpressionTokenizer.tokenizeExpression(expression);
        for (String token : tokens) {
            if(ExpressionOperator.isLogicOperator(token)) {
                hasOperator = true;
            }
            if(token.equals("(")) {
                parenthesesOpened++;
            }
            if(token.equals(")")) {
                parenthesesOpened--;
            }
        }

        return hasOperator && parenthesesOpened == 0;
    }
}
