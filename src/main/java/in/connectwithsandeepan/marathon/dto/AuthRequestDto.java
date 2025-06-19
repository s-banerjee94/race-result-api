package in.connectwithsandeepan.marathon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Authentication request data transfer object")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthRequestDto {
    @Schema(description = "Username of the user, min length: 6 max length: 15", example = "john_doe")
    private String username;

    @Schema(description = "Password of the user, min length: 8 max length: 20", example = "password123")
    private String password;
}
