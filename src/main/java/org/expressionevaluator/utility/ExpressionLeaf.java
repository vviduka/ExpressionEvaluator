package org.expressionevaluator.utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Value
@AllArgsConstructor
public class ExpressionLeaf {
    String lhsExpression;
    String rhsExpression;
    ExpressionOperator operator;
    Boolean result;
}
