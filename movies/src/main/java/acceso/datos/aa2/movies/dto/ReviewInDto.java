package acceso.datos.aa2.movies.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ReviewInDto {
    @NotNull(message = "Comment is required")
    @NotBlank(message = "Comment cannot be empty")
    @Size(min = 10, max = 500, message = "Comment must be between 10 and 500 characters")
    private String comment;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 10")
    @Max(value = 10, message = "Rating must be between 1 and 10")
    private float rating;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reviewDate;

    private Boolean recommended;
    private Boolean spoiler;

    @NotNull(message = "User ID is required")
    private Long userId;
}
