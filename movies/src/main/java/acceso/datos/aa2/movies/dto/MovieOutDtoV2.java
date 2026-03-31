package acceso.datos.aa2.movies.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class MovieOutDtoV2 extends MovieOutDto {
    private Long daysUntilRelease;
    private String directorName;

    public MovieOutDtoV2(Long id, String title, String synopsis, String genre,
                         LocalDate releaseDate, Integer duration, Float averageRating,
                         String imageUrl, StudioOutDto studio, DirectorOutDto director,
                         Long daysUntilRelease, String directorName) {
        super(id, title, synopsis, genre, releaseDate, duration, averageRating, imageUrl, studio, director);
        this.daysUntilRelease = daysUntilRelease;
        this.directorName = directorName;
    }
}
