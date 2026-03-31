package acceso.datos.aa2.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class StudioOutDto {
    private Long id;
    private String name;
    private String country;
    private Boolean active;
    private Double latitude;
    private Double longitude;
}
