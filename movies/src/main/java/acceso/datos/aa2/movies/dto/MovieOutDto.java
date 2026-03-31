package acceso.datos.aa2.movies.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieOutDto {
    private Long id;
    private String title;
    private String synopsis;
    private String genre;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    private Integer duration;
    private Float averageRating;
    private String imageUrl;

    private StudioOutDto studio;
    private DirectorOutDto director;
}
