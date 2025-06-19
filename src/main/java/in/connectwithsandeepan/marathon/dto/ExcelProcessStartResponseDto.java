package in.connectwithsandeepan.marathon.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Response object for starting the Excel processing job")
public class ExcelProcessStartResponseDto {
    @Schema(description = "Indicates whether the job was started successfully", example = "Job started successfully")
    private String message;
    @Schema(description = "Indicates the status of the job", example = "Job is running")
    private String status;
    @Schema(description = "Indicates the number of results that were deleted before starting the job", example = "10")
    private String deletedResultsCount;
}
