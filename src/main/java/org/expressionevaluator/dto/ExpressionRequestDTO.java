package org.expressionevaluator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.expressionevaluator.validator.ExpressionConstraint;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpressionRequestDTO {

    @NotEmpty(message = "Name of name cannot be empty.")
    @Min(value = 3, message = "Name needs to have at least minimum 3 letters.")
    private String name;

    @NotEmpty(message = "Name of expression cannot be empty.")
    //@ExpressionConstraint
    private String expression;
}
