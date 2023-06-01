package org.expressionevaluator.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.expressionevaluator.dto.APIResponse;
import org.expressionevaluator.dto.EvaluateExpressionRequestDTO;
import org.expressionevaluator.dto.ExpressionDTO;
import org.expressionevaluator.dto.ExpressionRequestDTO;
import org.expressionevaluator.mapper.ExpressionMapper;
import org.expressionevaluator.service.ExpressionEvaluatorService;
import org.expressionevaluator.service.impl.ExpressionEvaluatorServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class ExpressionEvaluatorController {

    private final static String SUCCESS = "success";

    private final ExpressionEvaluatorService expressionEvaluatorService;
    private final ExpressionMapper expressionMapper;

    @PostMapping("/expression")
    public ResponseEntity<APIResponse<Long>> createExpression(@Valid @RequestBody ExpressionRequestDTO expressionRequestDTO) {
        ExpressionDTO expressionDTO = expressionMapper.expressionRequestDTOToExpressionDTO(expressionRequestDTO);
        Long expressionId = expressionEvaluatorService.createExpression(expressionDTO);
        return new ResponseEntity<>( getAPIResponse(expressionId), HttpStatus.CREATED);
    }

    @GetMapping("/expression/{expressionId}")
    public ResponseEntity<APIResponse<ExpressionDTO>> fetchExpression(@PathVariable("expressionId") Long employeeId) {
        ExpressionDTO expressionDTO = expressionEvaluatorService.fetchExpression(employeeId);
        return new ResponseEntity<>(getAPIResponse(expressionDTO), HttpStatus.OK);
    }

    @PutMapping("/expression/update/{expressionId}")
    public ResponseEntity<APIResponse<Long>> updateExpression(@PathVariable("expressionId") Long employeeId,
                                                              @Valid @RequestBody ExpressionRequestDTO expressionRequestDTO) {
        ExpressionDTO expressionDTO = expressionMapper.expressionRequestDTOToExpressionDTO(expressionRequestDTO);
        expressionEvaluatorService.updateExpression(employeeId, expressionDTO);
        return new ResponseEntity<>(getAPIResponse(employeeId), HttpStatus.CREATED);
    }

    @DeleteMapping("/expression/delete/{expressionId}")
    public ResponseEntity<APIResponse<Long>> deleteExpression(@PathVariable("expressionId") Long employeeId) {
        expressionEvaluatorService.deleteExpression(employeeId);
        return new ResponseEntity<>(getAPIResponse(employeeId), HttpStatus.OK);
    }

    @PostMapping("/evaluate")
    public ResponseEntity<APIResponse<Boolean>> createExpression(@Valid @RequestBody EvaluateExpressionRequestDTO evaluateExpressionRequestDTO) {
        Boolean result = expressionEvaluatorService.evaluateExpression(evaluateExpressionRequestDTO.getExpressionId(), evaluateExpressionRequestDTO.getJsonString());
        return new ResponseEntity<>( getAPIResponse(result), HttpStatus.OK);
    }

    private <T> APIResponse<T> getAPIResponse(T result) {
        return APIResponse
                .<T>builder()
                .status(SUCCESS)
                .results(result)
                .build();
    }

}
