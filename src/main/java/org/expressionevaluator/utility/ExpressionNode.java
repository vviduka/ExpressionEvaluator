package org.expressionevaluator.utility;

import lombok.Data;

import java.util.List;

@Data
public class ExpressionNode {
    private String expression;
    private ExpressionOperator operator;
    private Integer depthLevel;
    private Boolean result;
    private List<ExpressionLeaf> expressionLeaves;

}
