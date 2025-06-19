package in.connectwithsandeepan.marathon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Schema(description = "User response data transfer object")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDto {
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Indicates whether the user is enabled", example = "true")
    private boolean enabled;

    @Schema(description = "Roles assigned to the user", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private Set<String> roles;

    @Schema(description = "Timestamp when the user was created", example = "2023-01-01T12:00:00Z")
    private Instant createdAt;

    @Schema(description = "Timestamp when the user was last updated", example = "2023-01-02T12:00:00Z")
    private Instant updatedAt;
}
