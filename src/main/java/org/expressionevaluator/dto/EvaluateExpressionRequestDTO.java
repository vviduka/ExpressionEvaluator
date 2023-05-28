package org.expressionevaluator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class EvaluateExpressionRequestDTO {

    @NotEmpty(message = "Expression ID cannot be empty or null.")
    private Long expressionId;
    @NotEmpty(message = "JSON object cannot be empty or null.")
    private String jsonString;

}
