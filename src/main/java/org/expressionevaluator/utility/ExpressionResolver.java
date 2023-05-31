package org.expressionevaluator.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.expressionevaluator.dto.ExpressionDTO;
import org.expressionevaluator.utility.tree.TreeNode;
import org.expressionevaluator.utility.tree.TreeNodeIterator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Component
@RequiredArgsConstructor
public class ExpressionResolver {

    private final ObjectMapper mapper;

    public boolean getResult(ExpressionDTO expressionDTO, String jsonInput) {
        try {
            JsonNode jsonNode = mapper.readTree(jsonInput);
            Stack<TreeNode<ExpressionNode>> nodeStack = new Stack<>();
            TreeNode<ExpressionNode> node = expressionDTO.getExpressionTree();
            populateStack(nodeStack, node);
            return evaluateExpression(jsonNode, nodeStack);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void populateStack(Stack<TreeNode<ExpressionNode>> nodeStack, TreeNode<ExpressionNode> node) {
        TreeNodeIterator<ExpressionNode> treeNodeIterator = new TreeNodeIterator<>(node);
        while (treeNodeIterator.hasNext()) {
            nodeStack.push(treeNodeIterator.next());
        }
    }

    private boolean evaluateExpression(JsonNode jsonNode, Stack<TreeNode<ExpressionNode>> nodeStack) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Boolean> leafResults = new ArrayList<>();
        while (!nodeStack.empty() && !nodeStack.peek().isRoot()) {
            TreeNode<ExpressionNode> node = nodeStack.pop();
            List<ExpressionLeaf> expressionLeaves = node.getData().getExpressionLeaves();
            if (expressionLeaves.isEmpty()) {
                leafResults.addAll(extractChildrenNodeResults(node));
            } else {
                collectLeavesResult(jsonNode, stringBuilder, leafResults, expressionLeaves);
            }
            if (leafResults.size() == 1) {
                leafResults.add(node.getChildren().get(0).getData().getResult());
            }
            boolean nodeResult = resolveResult(leafResults, node.getData().getOperator());
            node.getData().setResult(nodeResult);
            leafResults.clear();
        }

        TreeNode<ExpressionNode> root = nodeStack.peek();
        if (root.getChildren() != null && !root.getChildren().isEmpty()) {
            return resolveResult(extractChildrenNodeResults(root), root.getData().getOperator());
        } else {
            //Simple expression tree
            collectLeavesResult(jsonNode, stringBuilder, leafResults, root.getData().getExpressionLeaves());
            if(leafResults.size() == 1) {
                return leafResults.get(0);
            }
            return resolveResult(leafResults, root.getData().getOperator());
        }
    }

    private void collectLeavesResult(JsonNode jsonNode, StringBuilder stringBuilder, List<Boolean> leafResults, List<ExpressionLeaf> expressionLeaves) {
        for (ExpressionLeaf leaf : expressionLeaves) {
            String jsonPointer = stringBuilder.append("/").append(leaf.getLhsExpression().replace(".", "/")).toString();
            leafResults.add(determineTypeAndCompare(jsonNode.at(jsonPointer), leaf.getOperator(), leaf.getRhsExpression()));
            stringBuilder.setLength(0);
        }
    }

    private boolean resolveResult(List<Boolean> leafResults, ExpressionOperator operator) {
        boolean result = false;
        if (operator == ExpressionOperator.AND) {
            result = leafResults.stream().allMatch(Boolean::booleanValue);
        } else if (operator == ExpressionOperator.OR) {
            result = leafResults.stream().anyMatch(Boolean::booleanValue);
        }
        return result;
    }

    private List<Boolean> extractChildrenNodeResults(TreeNode<ExpressionNode> root) {
        List<Boolean> results = new ArrayList<>();
        List<TreeNode<ExpressionNode>> rootChildren = root.getChildren();
        for (TreeNode<ExpressionNode> child : rootChildren) {
            results.add(child.getData().getResult());
        }
        return results;
    }

    private boolean determineTypeAndCompare(JsonNode value, ExpressionOperator operator, String valueToBeCompared) {
        if (value.isMissingNode()) {
            throw new IllegalArgumentException("Node that you are looking for is null or does not exist.");
        }

        boolean result = false;
        if (value.isTextual()) {
            String textValue = value.asText();
            result = compareText(textValue, operator, formatString(valueToBeCompared));
        } else if (value.isNumber()) {
            double numericValue = value.asDouble();
            result = compareNumeric(numericValue, operator, valueToBeCompared);
        } else if (value.isBoolean()) {
            boolean booleanValue = value.asBoolean();
            result = compareBoolean(booleanValue, operator, valueToBeCompared);
        } else if (valueToBeCompared.equals("null")) {
            result = compareNull(value, operator);
        }

        return result;
    }

    private String formatString(String valueToBeCompared) {
        return valueToBeCompared.replaceAll("\"", "").trim();
    }

    private boolean compareText(String value, ExpressionOperator operator, String valueToBeCompared) {
        return switch (operator) {
            case EQUALS -> value.equals(valueToBeCompared);
            case NOT_EQUALS -> !value.equals(valueToBeCompared);
            default -> false;
        };
    }

    private boolean compareNumeric(double value, ExpressionOperator operator, String valueToBeCompared) {
        double comparisonValue = Double.parseDouble(valueToBeCompared);

        return switch (operator) {
            case EQUALS -> value == comparisonValue;
            case NOT_EQUALS -> value != comparisonValue;
            case GREATER_THAN -> value > comparisonValue;
            case LESSER_THAN -> value < comparisonValue;
            case GREATER_OR_EQUALS_THAN -> value >= comparisonValue;
            case LESSER_OR_EQUALS_THAN -> value <= comparisonValue;
            default -> false;
        };
    }

    private boolean compareBoolean(boolean value, ExpressionOperator operator, String valueToBeCompared) {
        boolean comparisonValue = Boolean.parseBoolean(valueToBeCompared);

        return switch (operator) {
            case EQUALS -> value == comparisonValue;
            case NOT_EQUALS -> value != comparisonValue;
            default -> false;
        };
    }

    private boolean compareNull(JsonNode value, ExpressionOperator operator) {
        return switch (operator) {
            case EQUALS -> value.isNull();
            case NOT_EQUALS -> !value.isNull();
            default -> false;
        };
    }
}