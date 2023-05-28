package org.expressionevaluator.service;

import org.expressionevaluator.dto.ExpressionDTO;

public interface ExpressionEvaluatorService {

    Long createExpression(ExpressionDTO expressionDTO);

    ExpressionDTO fetchExpression(Long expressionId);

    void updateExpression(Long expressionId, ExpressionDTO expressionDTO);

    void deleteExpression(Long expressionId);


}
