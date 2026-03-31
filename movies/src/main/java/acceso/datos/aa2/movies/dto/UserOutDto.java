package acceso.datos.aa2.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserOutDto {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String email;
    private Boolean premium;
}
