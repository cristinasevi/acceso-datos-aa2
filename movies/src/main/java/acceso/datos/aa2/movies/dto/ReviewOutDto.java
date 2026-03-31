package acceso.datos.aa2.movies.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ReviewOutDto {
    private Long id;
    private String comment;
    private float rating;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reviewDate;

    private Boolean recommended;
    private Boolean spoiler;
    private String username;
    private String movieTitle;
}
