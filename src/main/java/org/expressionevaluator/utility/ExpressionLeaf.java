package org.expressionevaluator.utility;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpressionLeaf {
    private String lhsExpression;
    private String rhsExpression;
    private ExpressionOperator operator;
    private Boolean result;
}
