package in.connectwithsandeepan.marathon.service;

import in.connectwithsandeepan.marathon.dto.CheckpointRequestDTO;
import in.connectwithsandeepan.marathon.dto.ResultRequestDTO;
import in.connectwithsandeepan.marathon.entity.Checkpoint;
import in.connectwithsandeepan.marathon.entity.Event;
import in.connectwithsandeepan.marathon.entity.EventCategory;
import in.connectwithsandeepan.marathon.entity.Result;
import in.connectwithsandeepan.marathon.repo.EventRepository;
import in.connectwithsandeepan.marathon.repo.ResultRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final EventRepository eventRepository;

    @Override
    public Result saveResult(Long eventId, ResultRequestDTO dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event not found with id: {}", eventId);
                    return new EntityNotFoundException("Event not found with id: " + eventId);
                });
        EventCategory category = event.getEventCategories().stream()
                .filter(cat -> cat.getCategoryName().equalsIgnoreCase(dto.getRaceCategory()))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Category '{}' not found in event {}", dto.getRaceCategory(), eventId);
                    return new IllegalArgumentException("Category '" + dto.getRaceCategory() + "' not found in event with id: " + eventId);
                });

        Result result = new Result();
        result.setBibNumber(dto.getBibNumber());
        result.setParticipantName(dto.getParticipantName());
        result.setGender(dto.getGender());
        result.setAgeCategory(dto.getAgeCategory());
        result.setOverAllRank(dto.getOverAllRank());
        result.setGenderRank(dto.getGenderRank());
        result.setAgeCategoryRank(dto.getAgeCategoryRank());
        result.setChipTime(dto.getChipTime());
        result.setGunTime(dto.getGunTime());
        result.setEvent(event);
        result.setCategory(category);

        if (dto.getCheckpointTimes() != null) {
            List<Checkpoint> checkpoints = new ArrayList<>();
            for (CheckpointRequestDTO cpDto : dto.getCheckpointTimes()) {
                Checkpoint checkpoint = new Checkpoint();
                checkpoint.setCheckpointNumber(cpDto.getCheckpointNumber());
                checkpoint.setTime(cpDto.getTime());
                checkpoint.setResult(result);
                checkpoints.add(checkpoint);
            }
            result.setCheckpointTimes(checkpoints);
        }

        Result savedResult = resultRepository.save(result);
        log.info("Result saved with id: {} for participant: {}", savedResult.getId(), savedResult.getParticipantName());
        return savedResult;
    }

    @Override
    public Result getResultById(Long id) {
        log.debug("Fetching result with id: {}", id);
        return resultRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("Result not found with id: {}", id);
                    return new EntityNotFoundException("Result not found with id: " + id);
                }
        );
    }

    @Override
    public List<Result> getAllResultsByEventId(Long eventId) {
        log.debug("Fetching all results for event id: {}", eventId);

        // Validate event exists
        if (!eventRepository.existsById(eventId)) {
            log.warn("Event not found with id: {}", eventId);
            throw new EntityNotFoundException("Event not found with id: " + eventId);
        }

        List<Result> results = resultRepository.findAllByEventIdWithCategoryAndCheckpoints(eventId);  // Simple, no ordering
        log.info("Found {} results for event id: {}", results.size(), eventId);
        return results;
    }

    @Override
    public Result updateResult(Long id, Result result) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteResult(Long id) {
        log.debug("Deleting result with id: {}", id);

        Result result = getResultById(id);
        resultRepository.delete(result);

        log.info("Result deleted with id: {}", id);
    }
}
