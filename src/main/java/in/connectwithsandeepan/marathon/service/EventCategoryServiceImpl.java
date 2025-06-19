package in.connectwithsandeepan.marathon.service;


import in.connectwithsandeepan.marathon.dto.EventCategoryRequestDto;
import in.connectwithsandeepan.marathon.entity.Event;
import in.connectwithsandeepan.marathon.entity.EventCategory;
import in.connectwithsandeepan.marathon.exception.DuplicateCategoryException;
import in.connectwithsandeepan.marathon.repo.EventCategoryRepository;
import in.connectwithsandeepan.marathon.repo.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventCategoryServiceImpl implements EventCategoryService {

    public static final String EVENT_NOT_FOUND_WITH_ID = "Event not found with id: ";
    public static final String CATEGORY_NOT_FOUND_WITH_ID = "Category not found with id: ";
    public static final String CATEGORY_HAS_RESULTS = "Cannot modify category - it has existing results";
    public static final String EVENT_NOT_FOUND_WITH_ID1 = "Event not found with id: {}";


    private final EventCategoryRepository eventCategoryRepository;
    private final EventRepository eventRepository;

    @Override
    public List<EventCategory> getAllCategoriesByEvent(Long eventId) {
        log.debug("Fetching all categories for event id: {}", eventId);

        validateEventExists(eventId);

        List<EventCategory> categories = eventCategoryRepository.findByEventIdOrderByFlagOffTimeAsc(eventId);
        log.info("Found {} categories for event id: {}", categories.size(), eventId);
        return categories;
    }

    @Override
    public EventCategory getCategoryById(Long eventId, Long categoryId) {
        log.debug("Fetching category id: {} for event id: {}", categoryId, eventId);

        validateEventExists(eventId);

        EventCategory category = eventCategoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category not found with id: {}", categoryId);
                    return new EntityNotFoundException(CATEGORY_NOT_FOUND_WITH_ID + categoryId);
                });

        if (!category.getEvent().getId().equals(eventId)) {
            log.warn("Category {} does not belong to event {}", categoryId, eventId);
            throw new IllegalArgumentException("Category does not belong to this event");
        }

        log.info("Fetched category: {} for event: {}", category.getCategoryName(), eventId);
        return category;
    }

    @Override
    public EventCategory addCategory(Long eventId, EventCategoryRequestDto request) {
        log.debug("Adding category to event id: {} with data: {}", eventId, request);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn(EVENT_NOT_FOUND_WITH_ID1, eventId);
                    return new EntityNotFoundException(EVENT_NOT_FOUND_WITH_ID + eventId);
                });


        if (event.hasResults()) {
            log.warn("Cannot add category to event {} - event has existing results", eventId);
            throw new IllegalStateException(CATEGORY_HAS_RESULTS);
        }

        boolean categoryExists = event.getEventCategories().stream().anyMatch(cat -> cat.getCategoryName().equalsIgnoreCase(request.getCategoryName()));
        if (categoryExists) {
            log.warn("Category with name '{}' already exists for event {}", request.getCategoryName(), eventId);
            throw new DuplicateCategoryException("Category with " + request.getCategoryName() + " name already exists for this event");
        }


        EventCategory category = new EventCategory();
        category.setCategoryName(request.getCategoryName());
        category.setFlagOffTime(request.getFlagOffTime());
        category.setEvent(event);

        category = eventCategoryRepository.save(category);
        log.info("Category '{}' added to event {} with id: {}", category.getCategoryName(), eventId, category.getId());
        return category;
    }

    @Override
    public EventCategory updateCategory(Long eventId, Long categoryId, EventCategoryRequestDto request) {
        log.debug("Updating category id: {} for event id: {} with data: {}", categoryId, eventId, request);

        EventCategory category = getCategoryById(eventId, categoryId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn(EVENT_NOT_FOUND_WITH_ID1, eventId);
                    return new EntityNotFoundException(EVENT_NOT_FOUND_WITH_ID + eventId);
                });
        boolean sameCategoryNameExist = event.getEventCategories().stream()
                .anyMatch(cat -> cat.getCategoryName().equalsIgnoreCase(request.getCategoryName())
                        && !cat.getId().equals(categoryId));
        if (sameCategoryNameExist) {
            log.warn("Category name '{}' already exists for event {}", request.getCategoryName(), eventId);
            throw new DuplicateCategoryException("Category with " + request.getCategoryName() + " name already exists for this event");
        }

        category.setCategoryName(request.getCategoryName());
        category.setFlagOffTime(request.getFlagOffTime());

        category = eventCategoryRepository.save(category);
        log.info("Category updated: {} for event: {}", category.getCategoryName(), eventId);
        return category;
    }

    @Override
    public void deleteCategory(Long eventId, Long categoryId) {
        log.debug("Deleting category id: {} from event id: {}", categoryId, eventId);

        EventCategory category = getCategoryById(eventId, categoryId);


        if (hasResults(categoryId)) {
            int resultCount = getResultCount(categoryId);
            log.warn("Cannot delete category {} - has {} existing results", categoryId, resultCount);
            throw new IllegalStateException(CATEGORY_HAS_RESULTS);
        }

        String categoryName = category.getCategoryName();
        eventCategoryRepository.delete(category);
        log.info("Category '{}' deleted from event {}", categoryName, eventId);
    }

    @Override
    public boolean hasResults(Long categoryId) {
        log.debug("Checking if category {} has results", categoryId);
        return eventCategoryRepository.hasResults(categoryId);
    }

    @Override
    public int getResultCount(Long categoryId) {
        log.debug("Getting result count for category {}", categoryId);
        return eventCategoryRepository.getResultCount(categoryId);
    }

    private void validateEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            log.warn(EVENT_NOT_FOUND_WITH_ID1, eventId);
            throw new EntityNotFoundException(EVENT_NOT_FOUND_WITH_ID + eventId);
        }
    }
}
