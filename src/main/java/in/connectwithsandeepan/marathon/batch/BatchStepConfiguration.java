package in.connectwithsandeepan.marathon.batch;

import in.connectwithsandeepan.marathon.dto.ResultRequestDTO;
import in.connectwithsandeepan.marathon.entity.Result;
import in.connectwithsandeepan.marathon.exception.ProcessorInitializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step processResultsStep(
            ResultItemReader reader,
            ResultItemProcessor processor,
            ResultItemWriter writer) {

        return new StepBuilder("processResultsStep", jobRepository)
                .<ResultRequestDTO, Result>chunk(1000, transactionManager) // Process 1000 records per chunk
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()                    // Enable fault tolerance
                .skipLimit(100)                     // Skip up to 100 bad records
                .skip(ProcessorInitializationException.class)       // Skip on validation/processing errors
                .skip(DataIntegrityViolationException.class) // Skip on duplicate key errors
                .retryLimit(3)                      // Retry failed operations 3 times
                .retry(TransientDataAccessException.class)   // Retry on transient DB errors
                .listener(stepExecutionListener())  // Add step listener for monitoring
                .build();
    }

    @Bean
    public StepExecutionListener stepExecutionListener() {
        return new StepExecutionListener() {

            @Override
            public void beforeStep(StepExecution stepExecution) {
                Long eventId = stepExecution.getJobParameters().getLong("eventId");
                String filePath = stepExecution.getJobParameters().getString("filePath");

                log.info("Starting result upload step for event: {} with file: {}", eventId, filePath);
                log.info("Step name: {}", stepExecution.getStepName());
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                log.info("Step execution completed: {}", stepExecution.getStepName());
                log.info("Records read: {}", stepExecution.getReadCount());
                log.info("Records written: {}", stepExecution.getWriteCount());
                log.info("Records skipped: {}", stepExecution.getSkipCount());
                log.info("Commits: {}", stepExecution.getCommitCount());

                if (stepExecution.getSkipCount() > 0) {
                    log.warn("Warning: {} records were skipped due to validation or processing errors",
                            stepExecution.getSkipCount());
                }

                if (stepExecution.getStatus() == BatchStatus.COMPLETED) {
                    log.info("Step completed successfully");
                } else {
                    log.error("Step completed with status: {}", stepExecution.getStatus());
                }

                return stepExecution.getExitStatus();
            }
        };
    }
}
