package acceso.datos.aa2.movies.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieUpdateRequest {
    private String title;
    private String synopsis;
    private LocalDate releaseDate;
    private Integer duration;
    private String genre;
    private String imageUrl;
    private Float averageRating;
    private Long studioId;
    private Long directorId;
}
