package sk.stuba.sdg.isbe.handlers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundCustomException extends RuntimeException {
    public NotFoundCustomException(String message) {
        super(message);
    }
}

