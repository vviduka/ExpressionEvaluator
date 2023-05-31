package org.expressionevaluator.utility;

import org.expressionevaluator.utility.tree.TreeNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.expressionevaluator.utility.tree.ExpressionTreeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionTreeBuilderTest {

    @ParameterizedTest
    @MethodSource("stringTreeProvider")
    public void testTokenizeExpression(String expression, List<String> nodeExpression) {
        TreeNode<ExpressionNode> expressionTree = ExpressionTreeBuilder.buildTree(expression);

        // Collect all node expressions
        List<String> result = collectNodeExpressions(expressionTree);

        // Perform assertions
        assertEquals(nodeExpression, result);
    }

    private static Stream<Arguments> stringTreeProvider() {
        return Stream.of(
//                Arguments.of("A AND B", Arrays.asList("A && B")),
//                Arguments.of("A && B", Arrays.asList("A && B")),
//                Arguments.of("A || B", Arrays.asList("A || B")),
//                Arguments.of("A OR B", Arrays.asList("A || B")),
//                Arguments.of("A < B", Arrays.asList("A < B")),
//                Arguments.of("{A ?B AND \"C\"}", Arrays.asList("{A ?B && \"C\"}")),
                Arguments.of("A == B AND (a != c)", Arrays.asList("A == B &&", "a != c")),
                Arguments.of("(\"A\" >= C) || c < b", Arrays.asList("(\"A\" >= C)", " || c < b"))
        );
    }

    private List<String> collectNodeExpressions(TreeNode<ExpressionNode> node) {
        List<String> expressions = new ArrayList<>();
        collectNodeExpressionsRecursively(node, expressions);
        return expressions;
    }

    private void collectNodeExpressionsRecursively(TreeNode<ExpressionNode> node, List<String> expressions) {
        ExpressionNode expressionNode = node.getData();
        expressions.add(expressionNode.getExpression());
        for (TreeNode<ExpressionNode> child : node.getChildren()) {
            collectNodeExpressionsRecursively(child, expressions);
        }
    }
}