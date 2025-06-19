package in.connectwithsandeepan.marathon.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;


@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableBatchProcessing
public class ResultUploadJobConfig {
    private final JobRepository jobRepository;

    @Bean
    public Job resultUploadJob(Step processResultsStep) {
        return new JobBuilder("resultUploadJob", jobRepository)
                .start(processResultsStep)
                .listener(jobExecutionListener())
                .build();
    }

    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {

            @Override
            public void beforeJob(JobExecution jobExecution) {
                JobParameters params = jobExecution.getJobParameters();
                Long eventId = params.getLong("eventId");
                String filePath = params.getString("filePath");

                log.info("Starting Result Upload Job");
                log.info("Job ID: {}", jobExecution.getId());
                log.info("Event ID: {}", eventId);
                log.info("File Path: {}", filePath);
                log.info("Start Time: {}", jobExecution.getStartTime());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                JobParameters params = jobExecution.getJobParameters();
                Long eventId = params.getLong("eventId");

                log.info("Result Upload Job Completed");
                log.info("Job ID: {}", jobExecution.getId());
                log.info("Event ID: {}", eventId);
                log.info("Status: {}", jobExecution.getStatus());
                log.info("End Time: {}", jobExecution.getEndTime());

                // Log step statistics
                for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
                    log.info("Records Read: {}", stepExecution.getReadCount());
                    log.info("Records Written: {}", stepExecution.getWriteCount());
                    log.info("Records Skipped: {}", stepExecution.getSkipCount());
                }

                if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                    log.info("Job completed successfully!");
                } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
                    log.error("Job failed!");
                }
            }
        };
    }

    @Bean("launcher")
    @Primary
    public JobLauncher asyncJobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}
