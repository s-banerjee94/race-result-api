package in.connectwithsandeepan.marathon.controller;


import in.connectwithsandeepan.marathon.exception.ErrorResponse;
import in.connectwithsandeepan.marathon.dto.AuthRequestDto;
import in.connectwithsandeepan.marathon.dto.AuthResponseDto;
import in.connectwithsandeepan.marathon.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "For login", description = "Endpoints for login or get jwt token")
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
@Slf4j
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Login endpoint",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful, returns JWT token",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Authentication failed, invalid credentials",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }

    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequest) {
        log.info("Login request received for user: {}", authRequest.getUsername());
        return ResponseEntity.ok(authService.login(authRequest));
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        log.error("Runtime exception occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                "Unauthorized",
                "Authentication failed: " + ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        log.error("Authentication failed for request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
