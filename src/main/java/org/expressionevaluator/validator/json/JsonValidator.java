package org.expressionevaluator.validator.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonValidator implements ConstraintValidator<JsonConstraint, String> {

    private final ObjectMapper mapper;
    @Override
    public boolean isValid(String jsonInput, ConstraintValidatorContext constraintValidatorContext) {
        try{
            mapper.readTree(jsonInput);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
