package in.connectwithsandeepan.marathon.controller;


import in.connectwithsandeepan.marathon.dto.CreateEventRequestDto;
import in.connectwithsandeepan.marathon.entity.Event;
import in.connectwithsandeepan.marathon.exception.ErrorResponse;
import in.connectwithsandeepan.marathon.service.EventService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Event Management", description = "APIs endpoints for managing events")
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;


    @Operation(summary = "Create a new event", description = "Creates a new event with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateEventRequestDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody CreateEventRequestDto eventRequest) {
        log.info("Request to create event: {}", eventRequest);
        Event createdEvent = eventService.createEvent(eventRequest);
        log.info("Event created with id: {}", createdEvent.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @Operation(summary = "Get event by ID", description = "Fetches an event by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))
            ),
            @ApiResponse(responseCode = "404", description = "Event not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))

            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@Parameter(description = "Event ID", required = true, example = "1") @PathVariable Long id) {
        log.info("Request to get event by id: {}", id);
        Event event = eventService.getEventById(id);
        log.info("Event fetched for id: {}", id);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "Get all events", description = "Fetches a list of all events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        log.info("Request to get all events");
        List<Event> events = eventService.getAllEvents();
        log.info("Number of events found: {}", events.size());
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Update an existing event", description = "Updates an existing event with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Event.class))
            ),
            @ApiResponse(responseCode = "404", description = "Event not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id,@Valid @RequestBody CreateEventRequestDto eventRequest) {
        log.info("Request to update event id: {} with data: {}", id, eventRequest);
        Event updatedEvent = eventService.updateEvent(id, eventRequest);
        log.info("Event updated with id: {}", updatedEvent.getId());
        return ResponseEntity.ok(updatedEvent);
    }

    @Operation(summary = "Delete an event", description = "Deletes an event by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@Parameter(description = "Event ID", required = true, example = "1") @PathVariable Long id) {
        log.info("Request to delete event by id: {}", id);
        eventService.deleteEvent(id);
        log.info("Event deleted with id: {}", id);
        return ResponseEntity.noContent().build();
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
        log.error("Event not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}

