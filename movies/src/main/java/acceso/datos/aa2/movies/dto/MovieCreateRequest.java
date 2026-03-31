package acceso.datos.aa2.movies.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String synopsis;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer duration;

    private String genre;
    private String imageUrl;
    private Float averageRating;
    private Long studioId;
    private Long directorId;
}
