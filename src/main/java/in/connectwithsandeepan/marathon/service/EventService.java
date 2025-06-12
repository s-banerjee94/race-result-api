package in.connectwithsandeepan.marathon.service;

import in.connectwithsandeepan.marathon.dto.CreateEventRequestDto;
import in.connectwithsandeepan.marathon.entity.Event;

import java.util.List;

/**
 * Service interface for managing Event entities and related operations.
 * Provides methods for creating, retrieving, updating, and deleting events.
 */
public interface EventService {
    /**
     * Creates a new event with the provided details.
     *
     * @param request the event creation request DTO
     * @return the created Event entity
     * @see Event
     */
    Event createEvent(CreateEventRequestDto request);

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id the event ID
     * @return the  Event entity
     * @see Event
     */
    Event getEventById(Long id);

    /**
     * Retrieves all events.
     *
     * @return a list of all Event entities
     * @see Event
     */
    List<Event> getAllEvents();

    /**
     * Updates an existing event with the provided details.
     *
     * @param id      the event ID
     * @param request the event update request DTO
     * @return the updated Event entity
     * @see Event
     */
    Event updateEvent(Long id, CreateEventRequestDto request);

    /**
     * Deletes an event by its unique identifier.
     *
     * @param id the event ID
     */
    void deleteEvent(Long id);

}
