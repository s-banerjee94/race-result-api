package in.connectwithsandeepan.marathon.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response object for Excel file upload operation")
public class ExcelFileUploadResponseDto {
    @Schema(description = "Indicates whether the file upload was successful", example = "File uploaded successfully")
    private String message;
    @Schema(description = "Indicates whether the file upload was successful", example = "Event has 100 existing results. They will be deleted when job starts.")
    private String warning;
}
