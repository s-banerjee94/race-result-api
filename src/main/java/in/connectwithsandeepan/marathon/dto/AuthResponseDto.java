package in.connectwithsandeepan.marathon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Schema(description = "Authentication response data transfer object")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponseDto {
    @Schema(description = "JWT token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Username of the authenticated user", example = "john_doe")
    private String username;

    @Schema(description = "Roles assigned to the user", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private List<String> roles;

    @Schema(description = "Expiration time of the token", example = "2023-12-31T23:59:59Z")
    private Instant expiresAt;
}
