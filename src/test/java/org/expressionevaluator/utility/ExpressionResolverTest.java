package org.expressionevaluator.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.expressionevaluator.dto.ExpressionDTO;
import org.expressionevaluator.utility.tree.ExpressionTreeBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class ExpressionResolverTest {

    private static final String JSON_INPUT_STRING_1 = "{\"customer\":{\"firstName\": \"JOHN\",\"lastName\": \"DOE\", \"address\":{\"city\": \"Chicago\",\"zipCode\": 1234, \"street\": \"56th\", \"houseNumber\": 2345},\"salary\": 99,\"type\": \"BUSINESS\"}}";


    @ParameterizedTest
    @MethodSource("expressionResolverArgumentProvider")
    public void when_getResultIsProvidedWithCorrectInput_should_returnCorrectResult(String jsonData, ExpressionDTO expressionDTO, boolean result) {
        ObjectMapper objectMapper = new ObjectMapper();
        ExpressionResolver expressionResolver = new ExpressionResolver(objectMapper);
        assertEquals(result, expressionResolver.getResult(expressionDTO,jsonData));
    }




    private static Stream<Arguments> expressionResolverArgumentProvider() {
        return Stream.of(
                Arguments.of(JSON_INPUT_STRING_1,
                        createExpressionDto("customer.firstName == \"JOHN\""),
                        true),
                Arguments.of(JSON_INPUT_STRING_1,
                        createExpressionDto("customer.firstName == \"ROBERT\""),
                        false),
                Arguments.of(JSON_INPUT_STRING_1,
                        createExpressionDto("customer.firstName == \"JOHN\" AND customer.address != null"),
                        true),
                Arguments.of(JSON_INPUT_STRING_1,
                        createExpressionDto("customer.address.city == \\\"Washington\\\" || customer.address.city == \"New York\""),
                        false),
                Arguments.of(JSON_INPUT_STRING_1,
                        createExpressionDto("((customer.firstName == \"JOHN\"  && (customer.lastName == \"DOE\" || customer.address.zipCode < 1000)) && customer.salary < 100) && ((customer.address != null && customer.address.houseNumber == 2345) || (customer.address.city == \"Washington\" || customer.address.city == \"New York\"))"),
                        true),
                Arguments.of(JSON_INPUT_STRING_1,
                        createExpressionDto("customer.firstName == \"ROBERT\" && (customer.lastName == \"DOE\" AND (customer.address.zipCode < 1000 || (customer.salary < 100 AND (customer.address.street == \"55th\" && customer.type == \"PERSONAL\"))))"),
                        false)
        );
    }

    private static ExpressionDTO createExpressionDto(String expression) {
        return ExpressionDTO.builder()
                            .expressionTree(ExpressionTreeBuilder.buildTree(expression))
                            .build();
    }

}