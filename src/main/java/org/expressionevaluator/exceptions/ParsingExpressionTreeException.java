package org.expressionevaluator.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ParsingExpressionTreeException extends RuntimeException {

    private String message;

    public ParsingExpressionTreeException(String message) {
        this.message = message;
    }
}
