package org.expressionevaluator.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.expressionevaluator.validation.json.JsonConstraint;

@Data
@AllArgsConstructor
public class EvaluateExpressionRequestDTO {

    @NotNull(message = "Expression ID cannot be null.")
    private Long expressionId;

    @NotEmpty(message = "JSON object cannot be empty")
    @NotNull(message = "JSON object cannot be null.")
    @JsonConstraint(message = "Not valid Json string.")
    private String jsonString;

}
