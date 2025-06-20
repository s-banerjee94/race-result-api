package in.connectwithsandeepan.marathon.batch;

import in.connectwithsandeepan.marathon.dto.ExcelFileUploadResponseDto;
import in.connectwithsandeepan.marathon.dto.ExcelProcessStartResponseDto;
import in.connectwithsandeepan.marathon.exception.ErrorResponse;
import in.connectwithsandeepan.marathon.exception.ExcelProcessException;
import in.connectwithsandeepan.marathon.exception.ExcelReadingException;
import in.connectwithsandeepan.marathon.repo.ResultRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Batch Result Upload", description = "API endpoints for bulk uploading marathon results from Excel files")
@RestController
@RequestMapping("/api/v1/events/{eventId}/results/batch")
@RequiredArgsConstructor
@Slf4j
public class BatchUploadController {

    public static final String TEMPLATE = """
            Bib Number,Participant Name,Gender,Race Category,Age Category,Overall Rank,Gender Rank,Age Category Rank,Chip Time,Gun Time,Checkpoint 1,Checkpoint 2
            M001,John Doe,M,Half Marathon,18-35,1,1,1,01:15:20,01:15:50,00:25:30,00:52:15
            F001,Jane Smith,F,Half Marathon,18-35,2,1,1,01:22:45,01:23:10,00:27:15,00:55:30
            """;
    public static final String EVENT = "event_";
    private final ResultUploadJobService jobService;
    private final ResultRepository resultRepository;
    private final JobExplorer jobExplorer;

    @Operation(
            summary = "Upload Excel file for batch processing",
            description = "Uploads and validates Excel file for later batch processing. File is saved in event-specific folder."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "File uploaded successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ExcelFileUploadResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file or validation error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ExcelFileUploadResponseDto> uploadFile(
            @Parameter(description = "Event ID", required = true)
            @PathVariable Long eventId,

            @Parameter(description = "Excel file containing results", required = true)
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("File upload request for event {}: {}", eventId, file.getOriginalFilename());

        validateFile(file);

        String filePath = saveFileInEventFolder(file, eventId);
        long existingResultsCount = resultRepository.countByEventId(eventId);
        ExcelFileUploadResponseDto uploadResponse = new ExcelFileUploadResponseDto();
        uploadResponse.setMessage("File uploaded successfully");


        if (existingResultsCount > 0) {
            uploadResponse.setWarning("Event has " + existingResultsCount + " existing results. They will be deleted when job starts.");
        }

        log.info("File uploaded successfully for event {}: {}", eventId, filePath);
        return ResponseEntity.ok(uploadResponse);
    }

    @Operation(
            summary = "Start batch processing job",
            description = "Starts the batch processing job for the uploaded file. Deletes existing results if any."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job started successfully"),
            @ApiResponse(responseCode = "400", description = "No file uploaded or job start failed"),
            @ApiResponse(responseCode = "404", description = "Event or file not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(
            value = "/start",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ExcelProcessStartResponseDto> startJob(
            @Parameter(description = "Event ID", required = true)
            @PathVariable Long eventId) {

        try {
            log.info("Job start request for event {}", eventId);

            String filePath = getEventFilePath(eventId);
            if (!Files.exists(Paths.get(filePath))) {
                throw new FileNotFoundException("No file found for event " + eventId);
            }

            long deletedCount = deleteExistingResults(eventId);

            JobExecution execution = jobService.startResultUpload(eventId, filePath);

            ExcelProcessStartResponseDto processStartResponse = new ExcelProcessStartResponseDto();
            processStartResponse.setMessage("Job started successfully");
            processStartResponse.setStatus(execution.getStatus().name());
            processStartResponse.setDeletedResultsCount(String.valueOf(deletedCount));


            log.info("Job started for event {} with job ID {} (deleted {} existing results)",
                    eventId, execution.getId(), deletedCount);
            return ResponseEntity.ok(processStartResponse);
        } catch (Exception e) {
            log.error("Job start failed for event {}: {}", eventId, e.getMessage(), e);
            throw new ExcelProcessException(e.getMessage());
        }
    }

    @Operation(
            summary = "Get job status and progress",
            description = "Returns current status and progress of the batch processing job"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Job not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(
            value = "/status/{jobId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> getJobStatus(
            @PathVariable Long eventId,
            @PathVariable Long jobId) {

        try {
            JobExecution jobExecution = jobExplorer.getJobExecution(jobId);

            if (jobExecution == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Job not found");
                error.put("jobId", jobId);
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("jobId", jobId);
            response.put("eventId", eventId);
            response.put("status", jobExecution.getStatus().name());

            StepExecution step = jobExecution.getStepExecutions().stream().findFirst().orElse(null);
            if (step != null) {
                response.put("recordsRead", step.getReadCount());
                response.put("recordsWritten", step.getWriteCount());
                response.put("recordsSkipped", step.getSkipCount());

                if (step.getReadCount() > 0) {
                    long totalProcessed = step.getWriteCount() + step.getSkipCount();
                    double progress = (double) totalProcessed / step.getReadCount() * 100.0;
                    response.put("progressPercentage", Math.round(progress));
                } else {
                    response.put("progressPercentage", 0);
                }
            }

            String message = switch (jobExecution.getStatus()) {
                case STARTED -> "Job is running...";
                case COMPLETED -> "Job completed successfully";
                case FAILED -> "Job failed";
                default -> jobExecution.getStatus().name();
            };
            response.put("message", message);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting job status: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("error", "Status retrieval failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(
            summary = "Download Excel template",
            description = "Downloads a template Excel file with the correct format for bulk result upload"
    )
    @GetMapping(
            value = "/template",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    public ResponseEntity<byte[]> downloadTemplate() {
        try {
            String template = createExcelTemplate();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=results-template.xlsx")
                    .body(template.getBytes());
        } catch (Exception e) {
            log.error("Error generating template: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null ||
                (!filename.toLowerCase().endsWith(".xlsx") && !filename.toLowerCase().endsWith(".xls"))) {
            throw new IllegalArgumentException("Only .xlsx and .xls files are supported");
        }

        long maxSize = 10L * 1024L * 1024L;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
        }
    }

    private String saveFileInEventFolder(MultipartFile file, Long eventId) throws IOException {
        Path eventDir = Paths.get("uploads", EVENT + eventId);
        if (!Files.exists(eventDir)) {
            Files.createDirectories(eventDir);
            log.info("Created directory: {}", eventDir.toAbsolutePath());
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        String filename = EVENT + eventId + "_results" + extension;
        Path filePath = eventDir.resolve(filename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("File saved: {} for event: {} (replaced if existed)", filePath.toAbsolutePath(), eventId);
        return filePath.toAbsolutePath().toString();
    }

    private String getEventFilePath(Long eventId) {
        Path eventDir = Paths.get("uploads", EVENT + eventId);

        Path xlsxPath = eventDir.resolve(EVENT + eventId + "_results.xlsx");
        if (Files.exists(xlsxPath)) {
            return xlsxPath.toAbsolutePath().toString();
        }

        Path xlsPath = eventDir.resolve(EVENT + eventId + "_results.xls");
        if (Files.exists(xlsPath)) {
            return xlsPath.toAbsolutePath().toString();
        }

        return xlsxPath.toAbsolutePath().toString();
    }

    private long deleteExistingResults(Long eventId) {
        try {
            log.info("Deleting existing results for event {}", eventId);

            long existingCount = resultRepository.countByEventId(eventId);

            if (existingCount > 0) {
                resultRepository.deleteCheckpointsByEventId(eventId);
                resultRepository.deleteByEventId(eventId);
                log.info("Deleted {} existing results for event {}", existingCount, eventId);
                return existingCount;
            } else {
                log.info("No existing results found for event {}", eventId);
                return 0;
            }

        } catch (Exception e) {
            log.error("Error deleting existing results for event {}: {}", eventId, e.getMessage(), e);
            throw new ExcelProcessException("Failed to delete existing results: " + e.getMessage());
        }
    }
/*

    private StepExecution getProcessingStepExecution(JobExecution jobExecution) {
        return jobExecution.getStepExecutions().stream()
                .filter(step -> "processResultsStep".equals(step.getStepName()))
                .findFirst()
                .orElse(null);
    }

    private String getStatusMessage(JobExecution jobExecution) {
        return switch (jobExecution.getStatus()) {
            case STARTING -> "Job is starting...";
            case STARTED -> "Job is running...";
            case COMPLETED -> "Job completed successfully";
            case FAILED -> "Job failed: " + jobExecution.getExitStatus().getExitDescription();
            case STOPPED -> "Job was stopped";
            case STOPPING -> "Job is stopping...";
            default -> jobExecution.getStatus().name();
        };
    }
    */
/*
    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        } else {
            return (seconds / 3600) + "h " + ((seconds % 3600) / 60) + "m " + (seconds % 60) + "s";
        }
    }
*/

    private String createExcelTemplate() {
        return TEMPLATE;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Problem in Excel File: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Problem in Excel File",
                ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFoundException(FileNotFoundException ex, HttpServletRequest request) {
        log.error("File Not Found: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "File Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(ExcelProcessException.class)
    public ResponseEntity<ErrorResponse> handleExcelProcessException(ExcelProcessException ex, HttpServletRequest request) {
        log.error("Excel processing exception: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "Problem in Excel Processing",
                ex.getMessage(),
                request.getRequestURI(),
                null,
                request.getMethod()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}