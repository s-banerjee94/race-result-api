package in.connectwithsandeepan.marathon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Schema(description = "Request object to create a new event category")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventCategoryRequestDto {
    @Schema(description = "Name of the event category", example = "Half Marathon")
    @Size(min = 1, max = 50, message = "Category name must be between 1 and 50 characters")
    @NotBlank(message = "Category name cannot be blank")
    private String categoryName;

    @Schema(description = "Flag off time for the event category in HH:mm format", example = "06:30:00")
    @NotNull(message = "Flag off time cannot be null")
    private LocalTime flagOffTime;
}
