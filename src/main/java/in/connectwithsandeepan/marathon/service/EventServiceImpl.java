package in.connectwithsandeepan.marathon.service;


import in.connectwithsandeepan.marathon.dto.CreateEventRequestDto;
import in.connectwithsandeepan.marathon.entity.Event;
import in.connectwithsandeepan.marathon.entity.EventCategory;
import in.connectwithsandeepan.marathon.repo.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    public static final String EVENT_NOT_FOUND_WITH_ID = "Event not found with id: ";
    private final EventRepository eventRepository;

    public Event createEvent(CreateEventRequestDto request) {
        log.debug("Creating event: {}", request);
        Event event = new Event();
        event.setEventName(request.getEventName());
        event.setEventDate(request.getEventDate());
        event.setEventDescription(request.getEventDescription());
        event.setOrganizerName(request.getOrganizerName());
        event.setOrganizerWebsite(request.getOrganizerWebsite());
        event.setImageUrl(request.getImageUrl());
        event.setCity(request.getCity());
        event.setState(request.getState());
        event.setCountry(request.getCountry());


        Event finalEvent = event;
        List<EventCategory> categories = request.getEventCategories().stream()
                .map(catDto -> {
                    EventCategory category = new EventCategory();
                    category.setCategoryName(catDto.getCategoryName());
                    category.setFlagOffTime(catDto.getFlagOffTime());
                    category.setEvent(finalEvent);
                    return category;
                })
                .toList();


        event.setEventCategories(categories);
        event = eventRepository.save(event);
        log.info("Event created with id: {}", event.getId());
        return event;
    }

    public Event getEventById(Long id) {
        log.debug("Fetching event by id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Event not found with id: {}", id);
                    return new EntityNotFoundException(EVENT_NOT_FOUND_WITH_ID + id);
                });
        log.info("Fetched event with id: {}", id);
        return event;
    }

    public List<Event> getAllEvents() {
        log.debug("Fetching all events");
        List<Event> events = eventRepository.findAll();
        log.info("Total events found: {}", events.size());
        return events;
    }

    public Event updateEvent(Long id, CreateEventRequestDto request) {
        log.debug("Updating event id: {} with data: {}", id, request);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Event not found for update with id: {}", id);
                    return new EntityNotFoundException(EVENT_NOT_FOUND_WITH_ID + id);
                });
        event.setEventName(request.getEventName());
        event.setEventDate(request.getEventDate());
        event.setEventDescription(request.getEventDescription());
        event.setOrganizerName(request.getOrganizerName());
        event.setOrganizerWebsite(request.getOrganizerWebsite());
        event.setImageUrl(request.getImageUrl());

        event = eventRepository.save(event);
        log.info("Event updated with id: {}", event.getId());
        return event;
    }

    public void deleteEvent(Long id) {
        log.debug("Deleting event by id: {}", id);
        if (!eventRepository.existsById(id)) {
            log.warn("Event not found for deletion with id: {}", id);
            throw new EntityNotFoundException(EVENT_NOT_FOUND_WITH_ID + id);
        }
        eventRepository.deleteById(id);
        log.info("Event deleted with id: {}", id);
    }


}
