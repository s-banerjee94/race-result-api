package in.connectwithsandeepan.marathon.controller;

import in.connectwithsandeepan.marathon.dto.ResultRequestDTO;
import in.connectwithsandeepan.marathon.entity.Result;
import in.connectwithsandeepan.marathon.exception.ErrorResponse;
import in.connectwithsandeepan.marathon.service.ResultServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Results Management", description = "API endpoints for managing results of marathon events")
@RestController
@RequestMapping("/api/v1/events/{eventId}/results")
@RequiredArgsConstructor
@Slf4j
public class ResultController {
    private final ResultServiceImpl resultService;


    @Operation(
            summary = "Create a new result for an event",
            description = "Creates a new participant result for the specified marathon event. " +
                    "Each result must have a unique bib number within the event."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Result created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResultRequestDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data - validation errors",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Bib number already exists for this event",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<Result> createResult(
            @Parameter(description = "Event ID", required = true, example = "1")
            @PathVariable Long eventId,
            @Parameter(description = "Result data", required = true)
            @RequestBody ResultRequestDTO result) {
        Result savedResult = resultService.saveResult(eventId, result);
        return ResponseEntity.ok(savedResult);
    }

    @Operation(summary = "Get result by ID", description = "Fetches a result by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Result retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Result.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Result not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{resultId}")
    public ResponseEntity<Result> getResultById(
            @Parameter(description = "Result ID", required = true, example = "1")
            @PathVariable Long resultId) {
        Result resultById = resultService.getResultById(resultId);
        return ResponseEntity.ok(resultById);
    }

    @Operation(summary = "Get all results for event", description = "Fetches all results for a specific event")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Results retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "array", implementation = Result.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping()
    public ResponseEntity<List<Result>> getAllResults(
            @Parameter(description = "Event ID", required = true, example = "1")
            @PathVariable Long eventId) {
        List<Result> results = resultService.getAllResultsByEventId(eventId);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Delete a result by id", description = "Deletes a result from the event by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Result deleted successfully - no content returned"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Result not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during deletion",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(
            @Parameter(description = "Result ID", required = true, example = "1")
            @PathVariable Long id) {

        log.info("Request to delete result with id: {}", id);
        resultService.deleteResult(id);
        log.info("Result deleted with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handelEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handelDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Data integrity violation: {} for request: {}", ex.getMessage(), request.getRequestURI());
        String userFriendlyMessage = extractUserFriendlyMessage(ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Duplicate Bib Number",
                userFriendlyMessage,
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        log.error("{}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    private String extractUserFriendlyMessage(String dbMessage) {
        if (dbMessage != null && dbMessage.contains("bib_number")) {
            return "Bib number already exists for this event. Please use a unique bib number.";
        }
        return "A data constraint violation occurred. Please check your input data.";
    }
}
