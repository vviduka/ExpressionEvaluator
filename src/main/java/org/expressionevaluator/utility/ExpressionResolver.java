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
            evaluateExpression(jsonNode, nodeStack);
            return node.getData().getResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void evaluateExpression(JsonNode jsonNode, Stack<TreeNode<ExpressionNode>> nodeStack) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Boolean> leafResults = new ArrayList<>();
        populateNodesResult(jsonNode, nodeStack, stringBuilder, leafResults);
    }

    private void populateNodesResult(JsonNode jsonNode, Stack<TreeNode<ExpressionNode>> nodeStack, StringBuilder stringBuilder, List<Boolean> leafResults) {
        while (!nodeStack.empty()) {
            TreeNode<ExpressionNode> node = nodeStack.pop();
            if (node.getData().getExpressionLeaves().isEmpty()) {
                //Nodes which consist of 2 nested expressions
                leafResults.addAll(extractChildrenNodeResults(node));
            } else {
                //Main logic for accessing json routes and collecting their result
                collectLeavesResult(jsonNode, stringBuilder, leafResults, node.getData().getExpressionLeaves());
            }
            if (leafResults.size() == 1) {
                if(node.getChildren().size() > 0){
                    //for children nodes that contains another node
                    leafResults.add(node.getChildren().get(0).getData().getResult());
                } else {
                    //for simple tree where we dont have any nodes
                    node.getData().setResult(leafResults.get(0));
                    leafResults.clear();
                    continue;
                }
            }
            // Set result for current node
            node.getData().setResult(resolveResult(leafResults, node.getData().getOperator()));
            leafResults.clear();
        }
    }

    //Populate stack so we can use Recursive decent algorithm
    private void populateStack(Stack<TreeNode<ExpressionNode>> nodeStack, TreeNode<ExpressionNode> node) {
        TreeNodeIterator<ExpressionNode> treeNodeIterator = new TreeNodeIterator<>(node);
        while (treeNodeIterator.hasNext()) {
            nodeStack.push(treeNodeIterator.next());
        }
    }

    //Evaluate expression on json input
    private void collectLeavesResult(JsonNode jsonNode, StringBuilder stringBuilder, List<Boolean> leafResults, List<ExpressionLeaf> expressionLeaves) {
        for (ExpressionLeaf leaf : expressionLeaves) {
            String jsonPointer = stringBuilder.append("/").append(leaf.getLhsExpression().replace(".", "/")).toString();
            leafResults.add(determineTypeAndCompare(jsonNode.at(jsonPointer), leaf.getOperator(), leaf.getRhsExpression()));
            stringBuilder.setLength(0);
        }
    }

    //Comparison of 2 leaves result
    private boolean resolveResult(List<Boolean> leafResults, ExpressionOperator operator) {
        if (operator == ExpressionOperator.AND) {
            return leafResults.stream().allMatch(Boolean::booleanValue);
        } else if (operator == ExpressionOperator.OR) {
            return leafResults.stream().anyMatch(Boolean::booleanValue);
        }
        return false;
    }

    //Collect children node results
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

        if (value.isTextual()) {
            String textValue = value.asText();
            return compareText(textValue, operator, formatString(valueToBeCompared));
        } else if (value.isNumber()) {
            double numericValue = value.asDouble();
            return compareNumeric(numericValue, operator, valueToBeCompared);
        } else if (value.isBoolean()) {
            boolean booleanValue = value.asBoolean();
            return compareBoolean(booleanValue, operator, valueToBeCompared);
        } else if (valueToBeCompared.equals("null")) {
            return compareNull(value, operator);
        }

        return false;
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
