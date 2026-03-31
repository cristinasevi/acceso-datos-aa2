package acceso.datos.aa2.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class StudioDto {
    private Long id;
    private String name;
    private String country;
    private Integer foundationYear;
    private String email;
    private String phone;
    private Boolean active;
}
