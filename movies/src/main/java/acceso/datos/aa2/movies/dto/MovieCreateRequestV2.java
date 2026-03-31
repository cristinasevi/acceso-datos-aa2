package acceso.datos.aa2.movies.dto;

import jakarta.validation.constraints.Max;
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
public class MovieCreateRequestV2 {
    @NotBlank(message = "Title is required")
    private String title;

    private String synopsis;

    @NotNull(message = "Release date is required")
    private LocalDate releaseDate;

    @Min(value = 1)
    private Integer duration;

    private String genre;
    private String imageUrl;
    private Float averageRating;

    // v2: acepta objetos anidados en lugar de IDs sueltos
    private StudioRef studio;
    private DirectorRef director;

    // v2: campo nuevo obligatorio
    @NotNull(message = "Rate is required")
    @Min(1) @Max(10)
    private Integer rate;

    @Data
    public static class StudioRef {
        private Long id;
    }

    @Data
    public static class DirectorRef {
        private Long id;
    }
}
