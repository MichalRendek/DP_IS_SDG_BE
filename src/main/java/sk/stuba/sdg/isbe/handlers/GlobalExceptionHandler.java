package sk.stuba.sdg.isbe.handlers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import sk.stuba.sdg.isbe.handlers.exceptions.*;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundCustomException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(NotFoundCustomException ex) {
        List<String> details = new ArrayList<>();
        String message = ex.getMessage();
        details.add(message);

        ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.NOT_FOUND, "Resource not found", details);
        logger.error(message, ex);

        return ResponseEntityBuilder.build(err);
    }

    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<Object> handleInvalidEntityException(InvalidEntityException ex) {
        List<String> details = new ArrayList<>();
        String message = ex.getMessage();
        details.add(message);

        ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.FORBIDDEN, "Invalid entity!", details);
        logger.error(message, ex);

        return ResponseEntityBuilder.build(err);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> handleRecipeExistsException(EntityExistsException ex) {
        List<String> details = new ArrayList<>();
        String message = ex.getMessage();
        details.add(message);

        ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.CONFLICT, "Entity exists!", details);
        logger.error(message, ex);

        return ResponseEntityBuilder.build(err);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<Object> handleInvalidOperationException(InvalidOperationException ex) {
        List<String> details = new ArrayList<>();
        String message = ex.getMessage();
        details.add(message);

        ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.FORBIDDEN, "Operation is not permitted!", details);
        logger.error(message, ex);

        return ResponseEntityBuilder.build(err);
    }

    @ExceptionHandler(DeviceErrorException.class)
    public ResponseEntity<Object> handleDeviceErrorException(DeviceErrorException ex) {
        List<String> details = new ArrayList<>();
        String message = ex.getMessage();
        details.add(message);

        ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.SERVICE_UNAVAILABLE, "Device is not working properly!", details);
        logger.error(message, ex);

        return ResponseEntityBuilder.build(err);
    }

}