package in.connectwithsandeepan.marathon.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest request) {

        return buildErrorResponse("Bad Request",
                "Malformed JSON request",
                request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse("Forbidden",
                "You do not have permission to access this resource",
                request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse("Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                request, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<ErrorResponse> buildErrorResponse(String error, String message, HttpServletRequest request, HttpStatus status) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                error,
                message,
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        return ResponseEntity.status(status).body(response);
    }
}
