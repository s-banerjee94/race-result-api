package in.connectwithsandeepan.marathon.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.connectwithsandeepan.marathon.entity.Event;
import in.connectwithsandeepan.marathon.entity.EventCategory;
import in.connectwithsandeepan.marathon.exception.BatchStartException;
import in.connectwithsandeepan.marathon.repo.EventCategoryRepository;
import in.connectwithsandeepan.marathon.repo.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultUploadJobService {

    private final JobLauncher jobLauncher;
    private final Job resultUploadJob;
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;


    public JobExecution startResultUpload(Long eventId, String filePath) {
        try {

            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));

            List<EventCategory> categories = event.getEventCategories();
            String eventJson = objectMapper.writeValueAsString(event);
            String categoriesJson = objectMapper.writeValueAsString(categories);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("eventId", eventId)
                    .addString("filePath", filePath)
                    .addString("eventData", eventJson)
                    .addString("categoriesData", categoriesJson)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            log.info("Launching result upload job for event {} with file {}", eventId, filePath);

            JobExecution execution = jobLauncher.run(resultUploadJob, jobParameters);

            log.info("Job launched with execution ID: {}", execution.getId());
            return execution;

        } catch (JobExecutionException e) {
            log.error("Failed to start result upload job: {}", e.getMessage(), e);
            throw new BatchStartException("Failed to start batch job", e);
        } catch (JsonProcessingException e) {
            throw new BatchStartException(e);
        }
    }
}