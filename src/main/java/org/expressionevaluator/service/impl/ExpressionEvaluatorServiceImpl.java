package org.expressionevaluator.service.impl;

import lombok.RequiredArgsConstructor;
import org.expressionevaluator.dto.ExpressionDTO;
import org.expressionevaluator.entity.Expression;
import org.expressionevaluator.exceptions.ResourceNotFoundException;
import org.expressionevaluator.mapper.ExpressionMapper;
import org.expressionevaluator.repository.ExpressionRepository;
import org.expressionevaluator.service.ExpressionEvaluatorService;
import org.expressionevaluator.utility.ExpressionResolver;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpressionEvaluatorServiceImpl implements ExpressionEvaluatorService {

    private final ExpressionRepository expressionRepository;
    private final ExpressionMapper expressionMapper;
    private final ExpressionResolver expressionResolver;

    @Override
    public Long createExpression(ExpressionDTO expressionDTO) {
        Expression expressionFromDb = expressionMapper.toExpressionEntity(expressionDTO);
        return expressionRepository.save(expressionFromDb).getId();
    }

    @Override
    public ExpressionDTO fetchExpression(Long expressionId) {
        Expression expressionFromDb = expressionRepository.findById(expressionId)
                .orElseThrow(() -> new ResourceNotFoundException("Expression", "id", expressionId));

        return expressionMapper.toExpressionDTO(expressionFromDb);
    }

    @Override
    public void updateExpression(Long expressionId, ExpressionDTO expressionDTO) {
        if(!expressionRepository.existsById(expressionId)) {
            throw new ResourceNotFoundException("Expression", "id", expressionId);
        }
        //Just in case that expression has been sent without proper id
        expressionDTO.setId(expressionId);
        Expression expressionForUpdate = expressionMapper.toExpressionEntity(expressionDTO);
        expressionRepository.save(expressionForUpdate);
    }

    @Override
    public void deleteExpression(Long expressionId) {
        expressionRepository.deleteById(expressionId);
    }

    @Override
    public boolean evaluateExpression(Long expressionId, String data) {
        Expression expressionFromDb = expressionRepository.findById(expressionId)
                .orElseThrow(() -> new ResourceNotFoundException("Expression", "id", expressionId));

        ExpressionDTO expressionDTO = expressionMapper.toExpressionDTO(expressionFromDb);
        return expressionResolver.getResult(expressionDTO, data);
    }


}
