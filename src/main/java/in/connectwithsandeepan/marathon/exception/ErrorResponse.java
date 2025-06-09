package in.connectwithsandeepan.marathon.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ErrorResponse",description = "Standard structure for API error responses")
public class ErrorResponse {
    @Schema(description = "The date and time when the error occurred", example = "2025-06-07T14:45:00")
    private LocalDateTime timestamp;

    @Schema(description = "Short description of the HTTP error", example = "Forbidden")
    private String error;

    @Schema(description = "Detailed message explaining the error", example = "You do not have permission to access this resource.")
    private String message;

    @Schema(description = "The requested URI that caused the error", example = "/api/v1/users")
    private String path;

    @Schema(description = "Map of specific field validation errors, if applicable (may be null)",
            example = "{\"email\": \"must not be blank\", \"password\": \"must be at least 8 characters\"}")
    private Map<String, String> fieldErrors;

    @Schema(description = "HTTP method used for the request", example = "POST")
    private String requestMethod;


    @Override
    public String toString() {
        return """
                {
                  "timestamp": "%s",
                  "error": "%s",
                  "message": "%s",
                  "path": "%s",
                  "requestMethod": "%s"
                }
                """.formatted(
                timestamp,
                error,
                message,
                path,
                requestMethod
        );
    }
}
