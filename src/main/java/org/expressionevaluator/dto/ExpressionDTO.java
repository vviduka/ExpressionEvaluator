package org.expressionevaluator.dto;

import lombok.Builder;
import lombok.Data;
import org.expressionevaluator.utility.ExpressionNode;
import org.expressionevaluator.utility.tree.TreeNode;

@Data
@Builder
public class ExpressionDTO {
    private Long id;
    private String name;
    private String expression;
    private TreeNode<ExpressionNode> expressionTree;
}
