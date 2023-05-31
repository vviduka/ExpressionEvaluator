package org.expressionevaluator.mapper;

import org.expressionevaluator.dto.EvaluateExpressionRequestDTO;
import org.expressionevaluator.dto.ExpressionDTO;
import org.expressionevaluator.dto.ExpressionRequestDTO;
import org.expressionevaluator.entity.Expression;
import org.expressionevaluator.utility.tree.ExpressionTreeBuilder;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

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

    public List<ExpressionDTO> toExpressionDTO(List<Expression> expressions) {
        return expressions.stream().map(this::toExpressionDTO).toList();
    }

    public List<Expression> toExpressionEntity(List<ExpressionDTO> expressions) {
        return expressions.stream().map(this::toExpressionEntity).toList();
    }

    public ExpressionDTO expressionRequestDTOToExpressionDTO(ExpressionRequestDTO expressionRequestDTO) {
        return  ExpressionDTO.builder()
                             .name(expressionRequestDTO.getName())
                             .expression(expressionRequestDTO.getExpression())
                             .expressionTree(ExpressionTreeBuilder.buildTree(expressionRequestDTO.getExpression()))
                             .build();
    }
}
