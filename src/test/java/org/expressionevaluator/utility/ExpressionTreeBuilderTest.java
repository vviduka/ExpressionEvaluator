package org.expressionevaluator.utility;

import org.expressionevaluator.exceptions.ParsingExpressionTreeException;
import org.expressionevaluator.utility.tree.TreeNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.expressionevaluator.utility.tree.ExpressionTreeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpressionTreeBuilderTest {

    @ParameterizedTest
    @MethodSource("correctStringTreeProvider")
    public void when_buildTreeIsProvidedWithCorrectInputString_should_returnCorrectTreeStructure(String expression, List<String> nodeExpression) {
        TreeNode<ExpressionNode> expressionTree = ExpressionTreeBuilder.buildTree(expression);
        List<String> result = collectNodeExpressions(expressionTree);
        assertEquals(nodeExpression, result);
    }

    @ParameterizedTest()
    @MethodSource("incorrectStringTreeProvider")
    public void when_buildTreeIsProvidedWithIncorrectInputString_should_throwException(String expression) {
        assertThrows( ParsingExpressionTreeException.class, () -> ExpressionTreeBuilder.buildTree(expression));
    }

    @Test
    public void when_buildTreeIsProvidedWithCorrectInputString_should_setCorrectExpressionLeaves() {

        String expressionString1 = "A == B";
        TreeNode<ExpressionNode> expressionTree1 = ExpressionTreeBuilder.buildTree(expressionString1);
        List<ExpressionLeaf> expressionLeavesList1 = expressionTree1.getData().getExpressionLeaves();
        assertEquals(1, expressionLeavesList1.size());
        assertLeaf(expressionLeavesList1.get(0), "A", "B", ExpressionOperator.EQUALS);

        //-------------------------------------------------------------------------------------------------//

        String expressionString2 = "A == B && C != D";
        TreeNode<ExpressionNode> expressionTree2 = ExpressionTreeBuilder.buildTree(expressionString2);
        List<ExpressionLeaf> expressionLeavesList2_1 = expressionTree2.getData().getExpressionLeaves();
        assertEquals(2, expressionLeavesList2_1.size());
        assertLeaf(expressionLeavesList2_1.get(0), "A", "B", ExpressionOperator.EQUALS);
        assertLeaf(expressionLeavesList2_1.get(1), "C", "D", ExpressionOperator.NOT_EQUALS);

        //-------------------------------------------------------------------------------------------------//

        String expressionString3 = "A == B && (C != D || E < F)";
        TreeNode<ExpressionNode> expressionTree3 = ExpressionTreeBuilder.buildTree(expressionString3);
        List<ExpressionLeaf> expressionLeavesList3_1 = expressionTree3.getData().getExpressionLeaves();

        assertEquals(1, expressionLeavesList3_1.size());
        assertLeaf(expressionLeavesList3_1.get(0), "A", "B", ExpressionOperator.EQUALS);

        List<TreeNode<ExpressionNode>> expressionChildrenList3_1 = expressionTree3.getChildren();
        assertEquals(1, expressionChildrenList3_1.size());
        List<ExpressionLeaf> expressionLeavesList3_2 = expressionChildrenList3_1.get(0).getData().getExpressionLeaves();
        assertEquals(2, expressionLeavesList3_2.size());
        assertLeaf(expressionLeavesList3_2.get(0), "C", "D", ExpressionOperator.NOT_EQUALS);
        assertLeaf(expressionLeavesList3_2.get(1), "E", "F", ExpressionOperator.LESSER_THAN);

        //-------------------------------------------------------------------------------------------------//

        String expressionString4 = "(A == B && C != D) || E < F";
        TreeNode<ExpressionNode> expressionTree4 = ExpressionTreeBuilder.buildTree(expressionString4);
        List<ExpressionLeaf> expressionLeavesList4_1 = expressionTree4.getData().getExpressionLeaves();

        assertEquals(1, expressionLeavesList4_1.size());
        assertLeaf(expressionLeavesList4_1.get(0), "E", "F", ExpressionOperator.LESSER_THAN);

        List<TreeNode<ExpressionNode>> expressionChildrenList4_1 = expressionTree4.getChildren();
        assertEquals(1, expressionChildrenList4_1.size());
        List<ExpressionLeaf> expressionLeavesList4_2 = expressionChildrenList4_1.get(0).getData().getExpressionLeaves();
        assertEquals(2, expressionLeavesList4_2.size());
        assertLeaf(expressionLeavesList4_2.get(0), "A", "B", ExpressionOperator.EQUALS);
        assertLeaf(expressionLeavesList4_2.get(1), "C", "D", ExpressionOperator.NOT_EQUALS);

        //-------------------------------------------------------------------------------------------------//

        String expressionString5 = "(A == B && G >= H) && (C != D || E < F)";
        TreeNode<ExpressionNode> expressionTree5 = ExpressionTreeBuilder.buildTree(expressionString5);
        List<ExpressionLeaf> expressionLeavesList5_1 = expressionTree5.getData().getExpressionLeaves();
        assertEquals(0, expressionLeavesList5_1.size());

        List<TreeNode<ExpressionNode>> expressionChildrenList5_1 = expressionTree5.getChildren();
        assertEquals(2, expressionChildrenList5_1.size());

        List<ExpressionLeaf> expressionLeavesList5_2 = expressionChildrenList5_1.get(0).getData().getExpressionLeaves();
        assertEquals(2, expressionLeavesList5_2.size());
        assertLeaf(expressionLeavesList5_2.get(0), "A", "B", ExpressionOperator.EQUALS);
        assertLeaf(expressionLeavesList5_2.get(1), "G", "H", ExpressionOperator.GREATER_OR_EQUALS_THAN);

        List<ExpressionLeaf> expressionLeavesList5_3 = expressionChildrenList5_1.get(1).getData().getExpressionLeaves();
        assertEquals(2, expressionLeavesList5_3.size());
        assertLeaf(expressionLeavesList5_3.get(0), "C", "D", ExpressionOperator.NOT_EQUALS);
        assertLeaf(expressionLeavesList5_3.get(1), "E", "F", ExpressionOperator.LESSER_THAN);

        //-------------------------------------------------------------------------------------------------//

        String expressionString6 = "(A == B && (G >= H || I <= J)) && (C != D || E < F)";
        TreeNode<ExpressionNode> expressionTree6 = ExpressionTreeBuilder.buildTree(expressionString6);
        List<ExpressionLeaf> expressionLeavesList6_1 = expressionTree6.getData().getExpressionLeaves();
        assertEquals(0, expressionLeavesList6_1.size());

        List<TreeNode<ExpressionNode>> expressionChildrenList6_1 = expressionTree6.getChildren();
        assertEquals(2, expressionChildrenList6_1.size());

        List<ExpressionLeaf> expressionLeavesList6_2 = expressionChildrenList6_1.get(0).getData().getExpressionLeaves();
        assertEquals(1, expressionLeavesList6_2.size());
        assertLeaf(expressionLeavesList6_2.get(0), "A", "B", ExpressionOperator.EQUALS);

        List<TreeNode<ExpressionNode>> expressionChildrenList6_2 = expressionChildrenList6_1.get(0).getChildren();
        assertEquals(1, expressionChildrenList6_2.size());
        List<ExpressionLeaf> expressionLeavesList6_3 = expressionChildrenList6_2.get(0).getData().getExpressionLeaves();
        assertEquals(2, expressionLeavesList6_3.size());
        assertLeaf(expressionLeavesList6_3.get(0), "G", "H", ExpressionOperator.GREATER_OR_EQUALS_THAN);
        assertLeaf(expressionLeavesList6_3.get(1), "I", "J", ExpressionOperator.LESSER_OR_EQUALS_THAN);


        List<ExpressionLeaf> expressionLeavesList6_4 = expressionChildrenList6_1.get(1).getData().getExpressionLeaves();
        assertEquals(2, expressionLeavesList6_4.size());
        assertLeaf(expressionLeavesList6_4.get(0), "C", "D", ExpressionOperator.NOT_EQUALS);
        assertLeaf(expressionLeavesList6_4.get(1), "E", "F", ExpressionOperator.LESSER_THAN);

    }

    private static void assertLeaf(ExpressionLeaf expressionLeaf, String lhsExpression, String rhsExpression, ExpressionOperator operator) {
        assertEquals(lhsExpression, expressionLeaf.getLhsExpression());
        assertEquals(rhsExpression, expressionLeaf.getRhsExpression());
        assertEquals(operator, expressionLeaf.getOperator());
    }


    private static Stream<Arguments> correctStringTreeProvider() {
        return Stream.of(
                Arguments.of("A AND B", List.of("A && B")),
                Arguments.of("A && B", List.of("A && B")),
                Arguments.of("A || B", List.of("A || B")),
                Arguments.of("A OR B", List.of("A || B")),
                Arguments.of("A < B", List.of("A < B")),
                Arguments.of("{A ?B AND \"C\"}", List.of("{A ?B && \"C\" }")),
                Arguments.of("A == B AND (a != c || d < f)", Arrays.asList("A == B &&", "a != c || d < f")),
                Arguments.of("(\"A\" >= C && F != null) || c < b", Arrays.asList("|| c < b", "\"A\" >= C && F != null")),
                Arguments.of("\"N\" <= \"M\" || (\"A\" >= \"B\" && \"D\" != null)", Arrays.asList("\"N\" <= \"M\" ||", "\"A\" >= \"B\" && \"D\" != null")),
                Arguments.of("(\"N\" <= \"M\" && \"R\" == \"F\") || (\"A\" >= \"B\" || \"D\" != null)", Arrays.asList("||", "\"N\" <= \"M\" && \"R\" == \"F\"", "\"A\" >= \"B\" || \"D\" != null"))
        );
    }

    private static Stream<Arguments> incorrectStringTreeProvider() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("()"),
                Arguments.of("(A AND B)"),
                Arguments.of("(A AND B)"),
                Arguments.of("A && B))"),
                Arguments.of("(A || B"),
                Arguments.of("A noOperator B"),
                Arguments.of("A <<>> B"),
                Arguments.of("{A ?B AND (\"C\"})"),
                Arguments.of("(A == B) AND (a != c || d < f)"),
                Arguments.of("(\"A\" >= C && (F != null)) || c < b"),
                Arguments.of("\"N\" <= \"M\" || ()\"A\" >= \"B\" && \"D\" != null)"),
                Arguments.of("(\"N\" <= \"M\" && \"R\" == \"F\" &&) || (\"A\" >= \"B\" || \"D\" != null))))))")
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