package acceso.datos.aa2.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserDto {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String email;
    private LocalDate registrationDate;
    private Boolean premium;
    private LocalDate birthDate;

    // Campo calculado
    private Integer age;
}