package in.connectwithsandeepan.marathon.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.connectwithsandeepan.marathon.dto.CheckpointRequestDTO;
import in.connectwithsandeepan.marathon.dto.ResultRequestDTO;
import in.connectwithsandeepan.marathon.entity.Checkpoint;
import in.connectwithsandeepan.marathon.entity.Event;
import in.connectwithsandeepan.marathon.entity.EventCategory;
import in.connectwithsandeepan.marathon.entity.Result;
import in.connectwithsandeepan.marathon.exception.ResultValidationException;
import in.connectwithsandeepan.marathon.repo.ResultRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@StepScope
@Component
@Slf4j
public class ResultItemProcessor implements ItemProcessor<ResultRequestDTO, Result> {
    private final Validator validator;
    private final ResultRepository resultRepository;

    private final ObjectMapper objectMapper;


    private final Long eventId;
    private final String eventDataJson;
    private final String categoriesDataJson;

    private Event cachedEvent;
    private Map<String, EventCategory> cachedCategoryMap;
    private Long currentEventId;

    public ResultItemProcessor(
            Validator validator,
            ResultRepository resultRepository,
            ObjectMapper objectMapper,
            @Value("#{jobParameters['eventId']}") Long eventId,
            @Value("#{jobParameters['eventData']}") String eventDataJson,
            @Value("#{jobParameters['categoriesData']}") String categoriesDataJson) {
        this.validator = validator;
        this.resultRepository = resultRepository;
        this.eventId = eventId;
        this.eventDataJson = eventDataJson;
        this.categoriesDataJson = categoriesDataJson;
        this.objectMapper = objectMapper;
    }

    private void ensureObjectsInitialized() {
        if (currentEventId == null || !currentEventId.equals(eventId)) {

            try {
                log.info("Converting JSON data to objects for event: {} (previous: {})", eventId, currentEventId);

                cachedEvent = objectMapper.readValue(eventDataJson, Event.class);

                TypeReference<List<EventCategory>> typeRef = new TypeReference<List<EventCategory>>() {};
                List<EventCategory> categories = objectMapper.readValue(categoriesDataJson, typeRef);

                cachedCategoryMap = categories.stream()
                        .collect(Collectors.toMap(EventCategory::getCategoryName, Function.identity()));

                currentEventId = eventId;

                log.info("Successfully converted JSON to objects - Event: {}, Categories: {}",
                        cachedEvent.getEventName(), cachedCategoryMap.size());

            } catch (JsonProcessingException e) {
                log.error("Failed to convert JSON to objects for event {}: {}", eventId, e.getMessage(), e);
                throw new RuntimeException("Failed to initialize processor for event " + eventId, e);
            }
        }
    }


    @Override
    public Result process(ResultRequestDTO dto) throws Exception {
        log.debug("Processing result for bib number: {}", dto.getBibNumber());

        ensureObjectsInitialized();
        validateDto(dto);
        checkBibAndCategory(dto);
        Result result = convertToEntity(dto);

        log.debug("Successfully processed result for bib number: {}", dto.getBibNumber());
        return result;
    }


    private void validateDto(ResultRequestDTO dto) {
        Set<ConstraintViolation<ResultRequestDTO>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new ResultValidationException("Validation failed for bib " + dto.getBibNumber() + ": " + errors);
        }
    }

    private void checkBibAndCategory(ResultRequestDTO dto) {
        if (resultRepository.existsByEvent_IdAndBibNumber(eventId, dto.getBibNumber())) {
            throw new ResultValidationException("Duplicate bib number: " + dto.getBibNumber());
        }
        if (!cachedCategoryMap.containsKey(dto.getRaceCategory())) {
            throw new ResultValidationException("Invalid category: " + dto.getRaceCategory());
        }


    }

    private Result convertToEntity(ResultRequestDTO dto) {
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

        result.setEvent(cachedEvent);
        result.setCategory(cachedCategoryMap.get(dto.getRaceCategory()));



        if (dto.getCheckpointTimes() != null && !dto.getCheckpointTimes().isEmpty()) {
            List<Checkpoint> checkpoints = new ArrayList<>();

            for (CheckpointRequestDTO cpDto : dto.getCheckpointTimes()) {
                if (cpDto.getTime() != null) {
                    Checkpoint checkpoint = new Checkpoint();
                    checkpoint.setCheckpointNumber(cpDto.getCheckpointNumber()); // Uses the number from DTO
                    checkpoint.setTime(cpDto.getTime());
                    checkpoint.setResult(result); // Set back reference
                    checkpoints.add(checkpoint);
                }
            }

            result.setCheckpointTimes(checkpoints);
        }

        return result;
    }
}


