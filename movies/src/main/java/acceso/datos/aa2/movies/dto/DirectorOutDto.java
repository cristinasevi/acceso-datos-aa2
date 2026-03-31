package acceso.datos.aa2.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DirectorOutDto {
    private Long id;
    private String name;
    private String nationality;
    private Boolean active;
}
