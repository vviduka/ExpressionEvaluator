package org.expressionevaluator.utility.tree;

import org.expressionevaluator.utility.ExpressionLeaf;
import org.expressionevaluator.utility.ExpressionNode;
import org.expressionevaluator.utility.ExpressionOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ExpressionTreeBuilder {

    private static int index;
    private static int depthLevel;

    public static TreeNode<ExpressionNode> buildTree(String expression) {
        List<String> tokens = ExpressionTokenizer.tokenizeExpression(expression);
        Stack<TreeNode<ExpressionNode>> stack = new Stack<>();

        ExpressionNode rootNode = new ExpressionNode();
        rootNode.setExpression(expression);
        rootNode.setDepthLevel(0);
        rootNode.setOperator(findRootOperator(tokens));

        index = 0;
        depthLevel = 0;

        TreeNode<ExpressionNode> root = new TreeNode<>(rootNode);

        if(expression.contains("(")) {
           return createSimpleTree(root);
        } else {
            stack.push(root);
            createChildren(tokens, stack);
            return root;
        }
    }

    private static TreeNode<ExpressionNode> createSimpleTree(TreeNode<ExpressionNode> root) {
        root.getData().setExpressionLeaves(createExpressionLeaves(root.getData()));
        return root;
    }

    public static List<ExpressionLeaf> createExpressionLeaves(ExpressionNode expressionNode) {
        String expression = expressionNode.getExpression();
        List<ExpressionLeaf> expressionLeafList = new ArrayList<>();
        if(expression != null) {
            String[] expressionLeaves = expression.trim().split(expressionNode.getOperator().getSplittingSymbol());
            for (String leaf: expressionLeaves) {
                //We know that there should be 3 tokens and 2nd token should be logical operator
                if(!leaf.trim().isEmpty()) {
                    List<String> tokens = ExpressionTokenizer.tokenizeExpression(leaf.trim());
                    if(tokens.size() != 3){
                        throw new IllegalArgumentException("Malformed logical expression!");
                    }
                    expressionLeafList.add(new ExpressionLeaf(tokens.get(0), tokens.get(2), ExpressionOperator.findBySymbol(tokens.get(1)), null));
                }
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
        //TODO: pogledati je li ovo dobro rijesenje
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

    private static ExpressionNode setExpressionNodeExpression(Stack<TreeNode<ExpressionNode>> stack) {
        ExpressionNode childNode = stack.pop().getData();
        childNode.setExpression(childNode.getExpression().trim());
        childNode.setExpressionLeaves(createExpressionLeaves(childNode));
        return childNode;
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
        for (ExpressionOperator operator : ExpressionOperator.values()) {
            if (operator.getSymbol().equals(token)) {
                return true;
            }
        }
        return false;
    }
}