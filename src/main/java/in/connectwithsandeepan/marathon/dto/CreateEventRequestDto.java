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
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Request object to create a new event")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateEventRequestDto {

    @Schema(description = "Name of the event", example = "Spring Half Marathon" , requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 6, max = 50, message = "Event name must be between 6 and 50 characters")
    @NotBlank(message = "Event name cannot be blank")
    private String eventName;

    @Schema(description = "Date of the event in YYYY-MM-DD format", example = "2025-11-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Event date cannot be null")
    private LocalDate eventDate;

    @Schema(description = "Brief description of the event", example = "A community half marathon for all age groups.")
    @Size(min = 15, max = 2000, message = "Event description must between 15 and 2000 characters")
    private String eventDescription;

    @Schema(description = "Name of the organizing body", example = "City Runners Club")
    @Size(min = 3, max = 50, message = "Organizer name must be between 3 and 50 characters")
    @NotBlank(message = "Organizer name cannot be blank")
    private String organizerName;

    @Schema(description = "Website of the organizing body", example = "https://cityrunners.org")
    @URL(message = "Organizer website must be a valid URL")
    private String organizerWebsite;

    @Schema(description = "City where the event takes place", example = "Kolkata")
    @Size(min = 2, max = 50, message = "City name must be between 2 and 50 characters")
    private String city;

    @Schema(description = "State or province where the event takes place", example = "West Bengal")
    @Size(min = 2, max = 50, message = "State name must be between 2 and 50 characters")
    private String state;

    @Schema(description = "Country where the event takes place", example = "India")
    @Size(min = 2, max = 50, message = "Country name must be between 2 and 50 characters")
    private String country;

    @Schema(description = "Image URL or identifier for the uploaded event image", example = "/static/images/event123.jpg")
    @Pattern(
            regexp = "^(|/[^\\s]+|(https?|ftp)://[^\\s/$.?#].[^\\s]*)$",
            message = "Must be a valid URL or path if provided"
    )
    private String imageUrl;

    @Schema(description = "List of categories associated with this event")
    @NotNull(message = "Event categories cannot be null")
    @Valid
    private List<EventCategoryRequestDto> eventCategories;
}
