package in.connectwithsandeepan.marathon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Tag(name = "UserRequestDto", description = "User request data transfer object")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestDto {
    @Schema(description = "Username of the user", example = "john_doe")
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 6, max = 15, message = "Username must be between 6 and 15 characters")
    private String username;

    @Schema(description = "Password of the user", example = "password123")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 20, message = "Password must be at least 8 characters long")
    private String password;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    @Email(message = "Email should be valid")
    @Size(min = 6, max = 50, message = "Email must be between 6 and 50 characters")
    private String email;
}
