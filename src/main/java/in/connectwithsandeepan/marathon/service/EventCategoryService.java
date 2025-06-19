package in.connectwithsandeepan.marathon.service;

import in.connectwithsandeepan.marathon.dto.EventCategoryRequestDto;
import in.connectwithsandeepan.marathon.entity.EventCategory;

import java.util.List;

public interface EventCategoryService {
    // Read operations
    List<EventCategory> getAllCategoriesByEvent(Long eventId);
    EventCategory getCategoryById(Long eventId, Long categoryId);

    // Write operations
    EventCategory addCategory(Long eventId, EventCategoryRequestDto request);
    EventCategory updateCategory(Long eventId, Long categoryId, EventCategoryRequestDto request);
    void deleteCategory(Long eventId, Long categoryId);

    // Utility operations
    boolean hasResults(Long categoryId);
    int getResultCount(Long categoryId);
}
