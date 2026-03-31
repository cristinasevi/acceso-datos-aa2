package acceso.datos.aa2.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MovieDto {
    private Long id;
    private String title;
    private String synopsis;
    private LocalDate releaseDate;
    private Integer duration;
    private String genre;
    private Float averageRating;
    private String imageUrl;

    // Campos calculados
    private Long daysUntilRelease;

    // Relaciones simplificadas
    private String directorName;
    private String studioName;
}
