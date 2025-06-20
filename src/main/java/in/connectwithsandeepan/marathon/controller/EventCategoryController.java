package in.connectwithsandeepan.marathon.controller;


import in.connectwithsandeepan.marathon.dto.EventCategoryRequestDto;
import in.connectwithsandeepan.marathon.entity.EventCategory;
import in.connectwithsandeepan.marathon.exception.DuplicateCategoryException;
import in.connectwithsandeepan.marathon.exception.ErrorResponse;
import in.connectwithsandeepan.marathon.service.EventCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Event Category Management",
        description = "API endpoints for managing race categories within marathon events.")

@RestController
@RequestMapping("/api/v1/events/{eventId}/categories")
@RequiredArgsConstructor
@Slf4j
public class EventCategoryController {
    private final EventCategoryService eventCategoryService;

    @Operation(summary = "Get all categories for an event",
            description = "Fetches all race categories for a specific marathon event")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "array", implementation = EventCategory.class)
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
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<EventCategory>> getAllCategories(
            @Parameter(description = "Event ID", required = true, example = "1")
            @PathVariable Long eventId) {

        log.info("Request to get all categories for event id: {}", eventId);
        List<EventCategory> categories = eventCategoryService.getAllCategoriesByEvent(eventId);
        log.info("Found {} categories for event id: {}", categories.size(), eventId);
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Get specific category by ID",
            description = "Fetches a specific race category for an event")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EventCategory.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Category does not belong to this event",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event or category not found",
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
    @GetMapping("/{categoryId}")
    public ResponseEntity<EventCategory> getCategoryById(
            @Parameter(description = "Event ID", required = true, example = "1")
            @PathVariable Long eventId,
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long categoryId) {

        log.info("Request to get category id: {} for event id: {}", categoryId, eventId);
        EventCategory category = eventCategoryService.getCategoryById(eventId, categoryId);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Add new category to event",
            description = "Creates a new race category for the specified event")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EventCategory.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or business rule violation",
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
                    description = "Category name already exists for this event",
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
    public ResponseEntity<EventCategory> addCategory(
            @Parameter(description = "Event ID", required = true, example = "1")
            @PathVariable Long eventId,
            @Parameter(description = "Category details", required = true)
            @Valid @RequestBody EventCategoryRequestDto request) {

        log.info("Request to add category to event id: {} with data: {}", eventId, request);
        EventCategory category = eventCategoryService.addCategory(eventId, request);
        log.info("Category '{}' added to event {} with id: {}", category.getCategoryName(), eventId, category.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @Operation(summary = "Update existing category",
            description = "Updates an existing race category. Limited updates allowed if category has results.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EventCategory.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or business rule violation",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event or category not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Category name already exists for this event",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Cannot modify category with existing results",
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
    @PutMapping("/{categoryId}")
    public ResponseEntity<EventCategory> updateCategory(
            @Parameter(description = "Event ID", required = true, example = "1")
            @PathVariable Long eventId,
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long categoryId,
            @Parameter(description = "Updated category details", required = true)
            @Valid @RequestBody EventCategoryRequestDto request) {

        log.info("Request to update category id: {} for event id: {} with data: {}", categoryId, eventId, request);
        EventCategory category = eventCategoryService.updateCategory(eventId, categoryId, request);
        log.info("Category '{}' updated for event: {}", category.getCategoryName(), eventId);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Delete category",
            description = "Deletes a race category from the event. Cannot delete if category has results.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Category deleted successfully - no content returned"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event or category not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Cannot delete category with existing results",
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
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Event ID", required = true, example = "1")
            @PathVariable Long eventId,
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long categoryId) {

        log.info("Request to delete category id: {} from event id: {}", categoryId, eventId);
        eventCategoryService.deleteCategory(eventId, categoryId);
        log.info("Category deleted from event: {}", eventId);
        return ResponseEntity.noContent().build();
    }



    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Invalid argument provided: {} for request: {}", ex.getMessage(), request.getRequestURI());
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

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        log.warn("Entity not found: {} for request: {}", ex.getMessage(), request.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        log.error("Event not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(DuplicateCategoryException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCategoryException(DuplicateCategoryException ex, HttpServletRequest request) {
        log.warn("Duplicate category attempted: {} for request: {}", ex.getMessage(), request.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Duplicate Category",
                ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        log.error("{}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Invalid operation attempted: {} for request: {}", ex.getMessage(), request.getRequestURI());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Unprocessable Entity",
                ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        log.error("{}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

}
