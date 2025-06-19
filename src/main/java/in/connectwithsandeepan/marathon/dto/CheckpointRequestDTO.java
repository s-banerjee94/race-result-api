package in.connectwithsandeepan.marathon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Checkpoint creation/update request")
public class CheckpointRequestDTO {

    @Schema(description = "Checkpoint sequence number", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "Checkpoint number must be at least 1")
    @Max(value = 20, message = "Checkpoint number must not exceed 20")
    private int checkpointNumber;

    @Schema(description = "Time at checkpoint (HH:mm:ss format)", example = "00:52:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Checkpoint time is required")
    private LocalTime time;
}
