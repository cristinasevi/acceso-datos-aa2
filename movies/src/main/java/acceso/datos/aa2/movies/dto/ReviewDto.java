package acceso.datos.aa2.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ReviewDto {
    private Long id;
    private String comment;
    private Integer rating;
    private LocalDate reviewDate;
    private Boolean recommended;
    private Boolean spoiler;

    // Relaciones simplificadas
    private Long userId;
    private String username;
    private Long movieId;
    private String movieTitle;
}
