package org.expressionevaluator.utility.tree;

import org.expressionevaluator.utility.ExpressionLeaf;
import org.expressionevaluator.utility.ExpressionNode;
import org.expressionevaluator.utility.ExpressionOperator;

import java.util.*;

public class ExpressionTreeBuilder {

    private static int index;
    private static int depthLevel;

    public static TreeNode<ExpressionNode> buildTree(String expression) {
        List<String> tokens = ExpressionTokenizer.tokenizeExpression(expression);
        Stack<TreeNode<ExpressionNode>> stack = new Stack<>();

        ExpressionNode rootNode = new ExpressionNode();
        rootNode.setExpression(expression);
        rootNode.setDepthLevel(0);

        if(!expression.contains("(")) {
            return createSimpleTree(rootNode, tokens);
        }

        rootNode.setOperator(findRootOperator(tokens));
        TreeNode<ExpressionNode> root = new TreeNode<>(rootNode);
        stack.push(root);

        index = 0;
        depthLevel = 0;

        createChildren(tokens, stack);

        return root;
    }

    private static TreeNode<ExpressionNode> createSimpleTree(ExpressionNode rootNode, List<String> tokens) {
        rootNode.setOperator(findLogicalOperator(tokens));
        rootNode.setExpressionLeaves(createExpressionLeaves(rootNode));
        return new TreeNode<>(rootNode);
    }

    public static ExpressionOperator findLogicalOperator(List<String> tokens) {
        return tokens.stream()
                .filter(token -> token.equals(ExpressionOperator.AND.getSymbol()) || token.equals(ExpressionOperator.OR.getSymbol()))
                .findFirst()
                .map(ExpressionOperator::findBySymbol)
                .orElseGet(() -> tokens.stream()
                        .filter(ExpressionOperator::isLogicOperator)
                        .map(ExpressionOperator::findBySymbol)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No operators found in the list of tokens.")));
    }

    public static List<ExpressionLeaf> createExpressionLeaves(ExpressionNode expressionNode) {
        String expression = expressionNode.getExpression();
        if(expression == null) {
            throw new IllegalArgumentException("Expression cannot be null!");
        }

        List<ExpressionLeaf> expressionLeafList = new ArrayList<>();
        List<String> expressionLeaves = Arrays.stream(expression.trim().split(expressionNode.getOperator().getSplittingSymbol()))
                                              .filter(str -> !str.isEmpty())
                                              .toList();


        if((expressionNode.getOperator() != ExpressionOperator.AND) && (expressionNode.getOperator() != ExpressionOperator.OR)) {
            expressionLeafList.add(new ExpressionLeaf(expressionLeaves.get(0).trim(), expressionLeaves.get(1).trim(), expressionNode.getOperator(), null));
            return expressionLeafList;
        }

        for (String leaf: expressionLeaves) {
            List<String> tokens = ExpressionTokenizer.tokenizeExpression(leaf.trim());
            if(tokens.size() == 3) {
                expressionLeafList.add(new ExpressionLeaf(tokens.get(0), tokens.get(2), ExpressionOperator.findBySymbol(tokens.get(1)), null));
            }
        }

        return expressionLeafList;
    }

    private static ExpressionOperator findRootOperator(List<String> tokens) {
        int rootLevel = 0;
        for (String token : tokens) {
            if (token.equals("(")) {
                rootLevel++;
            } else if (token.equals(")")) {
                rootLevel--;
            } else if (rootLevel == 0 && isLogicalOperator(token)) {
                return ExpressionOperator.findBySymbol(token);
            }
        }

        throw new IllegalArgumentException("Operator not found!");

    }

    private static void createChildren(List<String> tokens, Stack<TreeNode<ExpressionNode>> stack) {
        while (index < tokens.size()) {
            String token = tokens.get(index);
            if (token.equals("(")) {
                depthLevel++;
                index++;
                stack.push(createExpressionNodeChild(stack.peek()));
            } else if (token.equals(")")) {
                depthLevel--;
                TreeNode<ExpressionNode> node = stack.pop();
                node.getData().setExpression(node.getData().getExpression().trim());
                node.getData().setExpressionLeaves(createExpressionLeaves(node.getData()));
                stack.peek().addChild(node);
                index++;
            } else if (isLogicalOperator(token) && depthLevel != 0) {
                setExpressionNodeOperator(stack, token);
                index++;
            } else {
                stack.peek().getData().setExpression(stack.peek().getData().getExpression() + token + " ");
                index++;
            }
        }
    }

    private static void setExpressionNodeOperator(Stack<TreeNode<ExpressionNode>> stack, String token) {
        ExpressionNode expressionNode = stack.peek().getData();
        expressionNode.setExpression(stack.peek().getData().getExpression() + " " + token + " ");
        expressionNode.setOperator(ExpressionOperator.findBySymbol(token));
    }

    private static TreeNode<ExpressionNode> createExpressionNodeChild(TreeNode<ExpressionNode> parent) {
        ExpressionNode child = new ExpressionNode();
        child.setDepthLevel(depthLevel);
        child.setExpression("");
        TreeNode<ExpressionNode> expressionTreeNode = new TreeNode<>(child);
        expressionTreeNode.setParent(parent);
        return expressionTreeNode;
    }

    private static boolean isLogicalOperator(String token) {
        return token.equals(ExpressionOperator.AND.getSymbol()) || token.equals(ExpressionOperator.OR.getSymbol());
    }
}