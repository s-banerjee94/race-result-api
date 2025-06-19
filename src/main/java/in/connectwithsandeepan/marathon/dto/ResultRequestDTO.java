package in.connectwithsandeepan.marathon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result creation/update request")
public class ResultRequestDTO {

    @Schema(description = "Unique bib number assigned to participant", example = "MUM001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Bib number is required")
    @Size(max = 20, message = "Bib number must not exceed 20 characters")
    private String bibNumber;

    @Schema(description = "Full name of the participant", example = "Sandeepan Banerjee", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Participant name is required")
    @Size(min = 2,max = 100, message = "Participant name must be between 2 and 100 characters")
    private String participantName;

    @Schema(description = "Gender of participant", example = "M")
    @Pattern(regexp = "^(M|F|O|Male|Female|OTHER)$", message = "Gender must be M, F, O, Male, or Female or OTHER")
    private String gender;

    @Schema(description = "Race category name (must match event category)", example = "Half Marathon (21K)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Race category is required")
    private String raceCategory;

    @Schema(description = "Age category of participant", example = "18-35")
    private String ageCategory;

    @Schema(description = "Overall rank in the race", example = "15")
    private String overAllRank;

    @Schema(description = "Rank within gender category", example = "12")
    private String genderRank;

    @Schema(description = "Rank within age category", example = "8")
    private String ageCategoryRank;

    @Schema(description = "Official chip time (HH:mm:ss format)", example = "01:15:20", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Chip time is required")
    private LocalTime chipTime;

    @Schema(description = "Gun time from race start (HH:mm:ss format)", example = "01:15:50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Gun time is required")
    private LocalTime gunTime;

    @Schema(description = "List of checkpoint times during the race")
    @Valid
    private List<CheckpointRequestDTO> checkpointTimes;
}