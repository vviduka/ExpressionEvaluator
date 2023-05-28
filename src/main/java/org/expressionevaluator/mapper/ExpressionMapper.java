package org.expressionevaluator.mapper;

import org.expressionevaluator.dto.ExpressionDTO;
import org.expressionevaluator.dto.ExpressionRequestDTO;
import org.expressionevaluator.entity.Expression;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
public class ExpressionMapper {

    public ExpressionDTO toExpressionDTO(@NotNull Expression expression) {
        return new ExpressionDTO(expression.getId(),
                                 expression.getName(),
                                 expression.getExpression(),
                                 expression.getConditions());
    }

    public Expression toExpressionEntity(@NotNull ExpressionDTO expressionDTO) {
        return new Expression(expressionDTO.getId(),
                              expressionDTO.getName(),
                              expressionDTO.getExpression(),
                              expressionDTO.getConditions());
    }

    public List<ExpressionDTO> toExpressionDTO(List<Expression> expressions) {
        return expressions.stream().map(this::toExpressionDTO).toList();
    }

    public List<Expression> toExpressionEntity(List<ExpressionDTO> expressions) {
        return expressions.stream().map(this::toExpressionEntity).toList();
    }

    public ExpressionDTO expressionRequestDTOToExpressionDTO(ExpressionRequestDTO expressionRequestDTO) {
        return new ExpressionDTO(null, expressionRequestDTO.getName(), expressionRequestDTO.getExpression(),null);
    }
}
