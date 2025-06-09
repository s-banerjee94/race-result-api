package in.connectwithsandeepan.marathon.controller;

import in.connectwithsandeepan.marathon.dto.UserRequestDto;
import in.connectwithsandeepan.marathon.dto.UserResponseDto;
import in.connectwithsandeepan.marathon.exception.ErrorResponse;
import in.connectwithsandeepan.marathon.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Tag(name = "User Management", description = "Endpoints for managing users")
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Save a new user",
            description = "Creates a new user with the provided details and returns the created user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User details to create a new user",
                    content = @Content(schema = @Schema(implementation = UserRequestDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully created",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    @PostMapping()
    public ResponseEntity<UserResponseDto> saveUser(@Valid @RequestBody UserRequestDto user) {
        log.info("Request to save user: {}", user.getUsername());
        UserResponseDto savedUser = userService.saveUser(user);
        log.info("User saved with id: {}", savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @Operation(
            summary = "Find user by username",
            description = "Returns user details for the given username.",
            security = {@SecurityRequirement(name = "bearer-key")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    @GetMapping("/username")
    public ResponseEntity<UserResponseDto> findByUsername(@RequestParam String username) {
        log.info("Request to find user by username: {}", username);
        UserResponseDto user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Find user by email",
            description = "Returns user details for the given email.",
            security = {@SecurityRequirement(name = "bearer-key")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    @GetMapping("/email")
    public ResponseEntity<List<UserResponseDto>> findByEmail(@RequestParam String email) {
        log.info("Request to find user by email: {}", email);
        List<UserResponseDto> user = userService.findAllByEmail(email);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Find user by id",
            description = "Returns user details for the given user id.",
            security = {@SecurityRequirement(name = "bearer-key")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    @GetMapping
    public ResponseEntity<UserResponseDto> findById(@RequestParam Long id) {
        log.info("Request to find user by id: {}", id);
        UserResponseDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Find all users",
            description = "Returns a list of all users.",
            security = {@SecurityRequirement(name = "bearer-key")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of users",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> findAllUsers() {
        log.info("Request to find all users");
        List<UserResponseDto> users = userService.findAllUsers();
        log.info("Number of users found: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Delete user by id",
            description = "Deletes the user with the given id.",
            security = {@SecurityRequirement(name = "bearer-key")},
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "User deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    @DeleteMapping
    public ResponseEntity<Void> deleteUserById(@RequestParam Long id) {
        log.info("Request to delete user by id: {}", id);
        userService.deleteUserById(id);
        log.info("User deleted with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update user enabled status",
            description = "Enable or disable a user by id.",
            security = {@SecurityRequirement(name = "bearer-key")},
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "User status updated"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    )
            }
    )
    @PatchMapping("/enabled")
    public ResponseEntity<Void> updateUserEnabledStatus(@RequestParam Long id, @RequestParam boolean enabled) {
        log.info("Request to update enabled status for user id: {} to {}", id, enabled);
        userService.updateUserEnabledStatus(id, enabled);
        log.info("User enabled status updated for id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update user roles",
            description = "Update the roles of a user. Only accessible by ADMIN.",
            security = {@SecurityRequirement(name = "bearer-key")},
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "User roles updated"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden"
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/roles")
    public ResponseEntity<Void> updateUserRoles(@RequestParam Long id, @RequestParam Set<String> roles) {
        log.info("Request to update roles for user id: {} to {}", id, roles);
        userService.updateUserRoles(id, roles);
        log.info("User roles updated for id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UsernameNotFoundException ex, HttpServletRequest request) {
        log.error("User not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Validation Failed",
                "One or more fields are invalid",
                request.getRequestURI(),
                fieldErrors,
                request.getMethod()
        );
        log.error("Validation failed: {}", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUser(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Error occurred: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
