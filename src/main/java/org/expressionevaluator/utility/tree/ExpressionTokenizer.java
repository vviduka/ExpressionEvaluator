package org.expressionevaluator.utility.tree;

import org.assertj.core.util.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionTokenizer {
    public static List<String> tokenizeExpression(String expression) {
        String normalizedExpression = normalizeExpression(expression);
        List<String> tokens = new ArrayList<>();
        //Extract string from double quotes
        String regex = "\"([^\"]*)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(normalizedExpression);

        int currentIndex = 0;
        while (matcher.find()) {
            int matchStart = matcher.start();
            int matchEnd = matcher.end();

            if (currentIndex != matchStart) {
                String subExpression = normalizedExpression.substring(currentIndex, matchStart);
                tokenizeSubExpression(subExpression, tokens);
            }

            String match = normalizedExpression.substring(matchStart, matchEnd);
            tokens.add(match);

            currentIndex = matchEnd;
        }

        if (currentIndex < normalizedExpression.length()) {
            String subExpression = normalizedExpression.substring(currentIndex);
            tokenizeSubExpression(subExpression, tokens);
        }

        return tokens;
    }

    private static void tokenizeSubExpression(String subExpression, List<String> tokens) {
        StringTokenizer tokenizer = new StringTokenizer(subExpression, "() ", true);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (!token.isEmpty()) {
                tokens.add(token);
            }
        }
    }

    public static String normalizeExpression(String expression) {
        return expression.replaceAll("AND", "&&").replaceAll("OR", "||");
    }
}
