package org.expressionevaluator.mapper;

import jakarta.validation.constraints.NotNull;
import org.expressionevaluator.dto.ExpressionDTO;
import org.expressionevaluator.dto.ExpressionRequestDTO;
import org.expressionevaluator.entity.Expression;
import org.expressionevaluator.utility.tree.ExpressionTreeBuilder;
import org.springframework.stereotype.Component;


@Component
public class ExpressionMapper {

    public ExpressionDTO toExpressionDTO(@NotNull Expression expression) {
        return ExpressionDTO.builder()
                            .id(expression.getId())
                            .name(expression.getName())
                            .expression(expression.getExpression())
                            .expressionTree(ExpressionTreeBuilder.buildTree(expression.getExpression()))
                            .build();

    }

    public Expression toExpressionEntity(@NotNull ExpressionDTO expressionDTO) {
        return new Expression(expressionDTO.getId(),
                              expressionDTO.getName(),
                              expressionDTO.getExpression());
    }

    public ExpressionDTO expressionRequestDTOToExpressionDTO(ExpressionRequestDTO expressionRequestDTO) {
        return  ExpressionDTO.builder()
                             .name(expressionRequestDTO.getName())
                             .expression(expressionRequestDTO.getExpression())
                             .expressionTree(ExpressionTreeBuilder.buildTree(expressionRequestDTO.getExpression()))
                             .build();
    }
}
