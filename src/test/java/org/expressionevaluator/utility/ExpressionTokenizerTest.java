package org.expressionevaluator.utility;

import org.expressionevaluator.utility.tree.ExpressionTokenizer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionTokenizerTest {

    @ParameterizedTest
    @MethodSource("stringProvider")
    public void testTokenizeExpression(String parameter, List<String> tokens) {
        List<String> result = ExpressionTokenizer.tokenizeExpression(parameter);
        assertEquals(tokens, result);
    }

    private static Stream<Arguments> stringProvider() {
        return Stream.of(
                Arguments.of("A AND B", Arrays.asList("A", "&&", "B")),
                Arguments.of("A && B", Arrays.asList("A", "&&", "B")),
                Arguments.of("(A || B)", Arrays.asList("(", "A", "||", "B", ")")),
                Arguments.of("(A OR B)", Arrays.asList("(", "A", "||", "B", ")")),
                Arguments.of("ABC AB AND", Arrays.asList("ABC", "AB", "&&")),
                Arguments.of("{A ?B AND \"C\"}", Arrays.asList("{A", "?B", "&&", "\"C\"", "}")),
                Arguments.of("((A))", Arrays.asList("(", "(", "A", ")", ")")),
                Arguments.of("(\"A\")", Arrays.asList("(", "\"A\"", ")"))
        );
    }
}
