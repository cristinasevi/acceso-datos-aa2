package acceso.datos.aa2.movies.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "actors")

public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Column
    @NotNull(message = "Nationality is required")
    private String nationality;

    @Column(name = "birth_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Column
    private Boolean active;

    @Column
    @Min(value = 0, message = "Awards count cannot be negative")
    private Integer awards;

    @Column
    @Size(max = 1000, message = "Biography cannot exceed 1000 characters")
    private String biography;

    @Column(name = "actor_type")
    private String actorType;

    @ManyToMany(mappedBy = "actors")
    private List<Movie> movies;
}
