package pl.pomaranczowi.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.pomaranczowi.backend.dto.ErrorResponse;

import java.util.NoSuchElementException;

/**
 * Global exception handler that catches exceptions thrown by controllers
 * and maps them to structured {@link ErrorResponse} DTOs with appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from {@code @Valid} annotated request bodies.
     *
     * @param ex the validation exception
     * @return 400 Bad Request with the first field validation error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ErrorResponse errorResponse = new ErrorResponse(400, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles {@link NoSuchElementException} for missing resources.
     *
     * @param ex the exception
     * @return 404 Not Found
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException ex) {
        ErrorResponse errorResponse = new ErrorResponse(404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles illegal argument exceptions for bad request data.
     *
     * @param ex the exception
     * @return 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(400, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles {@link RuntimeException} by mapping message content to status codes:
     * <ul>
     *   <li>"already exists" → 409 Conflict</li>
     *   <li>"Invalid email or password" → 401 Unauthorized</li>
     *   <li>Other → 500 Internal Server Error</li>
     * </ul>
     *
     * @param ex the runtime exception
     * @return error response with the mapped HTTP status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        int status = 500;

        if (message != null) {
            if (message.contains("already exists")) {
                status = 409;
            } else if (message.contains("Invalid email or password")) {
                status = 401;
            }
        }

        ErrorResponse errorResponse = new ErrorResponse(status, message);
        return ResponseEntity.status(HttpStatus.valueOf(status)).body(errorResponse);
    }

    /**
     * Catch-all for any unhandled exceptions.
     *
     * @param ex the exception
     * @return 500 Internal Server Error with a generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(500, "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
