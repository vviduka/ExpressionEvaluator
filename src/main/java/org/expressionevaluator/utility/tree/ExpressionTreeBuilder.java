package org.expressionevaluator.utility.tree;

import org.expressionevaluator.exceptions.ParsingExpressionTreeException;
import org.expressionevaluator.utility.ExpressionLeaf;
import org.expressionevaluator.utility.ExpressionNode;
import org.expressionevaluator.utility.ExpressionOperator;

import java.util.*;

public class ExpressionTreeBuilder {

    public static TreeNode<ExpressionNode> buildTree(String expression) {
        List<String> tokens = ExpressionTokenizer.tokenizeExpression(expression);
        Stack<TreeNode<ExpressionNode>> stack = new Stack<>();

        ExpressionNode rootNode = new ExpressionNode();
        parseRootExpression(tokens, rootNode, expression.contains("("));
        TreeNode<ExpressionNode> root = new TreeNode<>(rootNode);
        stack.push(root);
        createChildren(tokens, stack);
        return root;
    }

    //we want first to get OR or AND operator but if that doesnt exist in expression return any
    //It plays a part in parsing simpleNodeConstructor
    public static ExpressionOperator findLogicalOperator(List<String> tokens) {
        return tokens.stream()
                .filter(token -> token.equals(ExpressionOperator.AND.getSymbol()) || token.equals(ExpressionOperator.OR.getSymbol()))
                .findFirst()
                .map(ExpressionOperator::findBySymbol)
                .orElseGet(() -> tokens.stream()
                        .filter(ExpressionOperator::isLogicOperator)
                        .map(ExpressionOperator::findBySymbol)
                        .findFirst()
                        .orElseThrow(() -> new ParsingExpressionTreeException("No operators found in the list of tokens.")));
    }

    public static List<ExpressionLeaf> createExpressionLeaves(ExpressionNode expressionNode) {
        if(expressionNode.getOperator() == null) {
            throw new ParsingExpressionTreeException("Operator not found in current node:" + expressionNode);
        }

        String expression = expressionNode.getExpression();
        if(expression == null) {
            throw new ParsingExpressionTreeException("Expression cannot be null!");
        }

        List<ExpressionLeaf> expressionLeafList = new ArrayList<>();
        List<String> expressionLeaves = Arrays.stream(expression.trim().split(expressionNode.getOperator().getSplittingSymbol()))
                                              .filter(str -> !str.isEmpty())
                                              .toList();

        //Check if its simple or complex logical expression. Complex are divided by OR or AND
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

    private static void parseRootExpression(List<String> tokens, ExpressionNode root, boolean isComplexTree) {
        int rootLevel = 0;
        StringBuilder rootExpression = new StringBuilder();

        if(!isComplexTree) {
            root.setOperator(findLogicalOperator(tokens));
        }

        for (String token : tokens) {
            if (token.equals("(")) {
                rootLevel++;
            } else if (token.equals(")")) {
                rootLevel--;
            } else if (rootLevel == 0) {
                if(isComplexTree && isLogicalOperator(token)) {
                    root.setOperator(ExpressionOperator.findBySymbol(token));
                }
                rootExpression.append(token).append(" ");
            }
        }
        root.setExpression(rootExpression.toString().trim());
        root.setExpressionLeaves(createExpressionLeaves(root));
    }

    private static void createChildren(List<String> tokens, Stack<TreeNode<ExpressionNode>> stack) {
        for(String token : tokens) {
            if (token.equals("(")) {
                stack.push(createExpressionNodeChild(stack.peek()));
            } else if (token.equals(")")) {
                saveExpressionAndAssignChildToParent(stack);
            } else if (isLogicalOperator(token) && !stack.peek().isRoot()) {
                setExpressionNodeOperator(stack, token);
            } else if(!stack.peek().isRoot()){
                stack.peek().getData().setExpression(stack.peek().getData().getExpression() + token + " ");
            }
        }
    }

    private static void saveExpressionAndAssignChildToParent(Stack<TreeNode<ExpressionNode>> stack) {
        try {
            TreeNode<ExpressionNode> node = stack.pop();
            node.getData().setExpression(node.getData().getExpression().trim());
            node.getData().setExpressionLeaves(createExpressionLeaves(node.getData()));
            stack.peek().addChild(node);
        } catch(EmptyStackException e) {
            throw new ParsingExpressionTreeException("There is no more items on stack!");
        }
    }

    private static void setExpressionNodeOperator(Stack<TreeNode<ExpressionNode>> stack, String token) {
        ExpressionNode expressionNode = stack.peek().getData();
        expressionNode.setExpression(stack.peek().getData().getExpression() + token + " ");
        expressionNode.setOperator(ExpressionOperator.findBySymbol(token));
    }

    private static TreeNode<ExpressionNode> createExpressionNodeChild(TreeNode<ExpressionNode> parent) {
        ExpressionNode child = new ExpressionNode();
        child.setExpression("");
        TreeNode<ExpressionNode> expressionTreeNode = new TreeNode<>(child);
        expressionTreeNode.setParent(parent);
        return expressionTreeNode;
    }

    private static boolean isLogicalOperator(String token) {
        return token.equals(ExpressionOperator.AND.getSymbol()) || token.equals(ExpressionOperator.OR.getSymbol());
    }
}