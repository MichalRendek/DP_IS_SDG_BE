package sk.stuba.sdg.isbe.handlers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class DeviceErrorException extends RuntimeException {
    public DeviceErrorException(String message) {
        super(message);
    }
}

