package org.expressionevaluator.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.expressionevaluator.validator.expression.ExpressionConstraint;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpressionRequestDTO {

    @NotEmpty(message = "Name cannot be empty.")
    private String name;

    @NotEmpty(message = "Expression is required.")
    @ExpressionConstraint(message = "Malformed expression.")
    private String expression;
}
