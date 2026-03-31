package acceso.datos.aa2.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ActorDto {
    private Long id;
    private String name;
    private String nationality;
    private LocalDate birthDate;
    private Boolean active;
    private Integer awards;
    private String biography;
    private String actorType;

    // Campo calculado
    private Integer age;
}
