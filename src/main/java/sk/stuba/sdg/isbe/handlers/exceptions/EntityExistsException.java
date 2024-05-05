package sk.stuba.sdg.isbe.handlers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EntityExistsException extends RuntimeException {
    public EntityExistsException(String message) {
        super(message);
    }
}

