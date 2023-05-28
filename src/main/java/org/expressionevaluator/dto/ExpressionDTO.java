package org.expressionevaluator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpressionDTO {
    private Long id;
    private String name;
    private String expression;
    private List<String> conditions;
}
