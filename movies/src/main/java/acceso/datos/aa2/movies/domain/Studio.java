package acceso.datos.aa2.movies.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "studios")

public class Studio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Column
    @NotNull(message = "Country is required")
    private String country;

    @Column(name = "foundation_year")
    @Min(value = 1888, message = "Foundation year must be after 1888")
    private Integer foundationYear;

    @Column
    @Email(message = "Email must be valid")
    private String email;

    @Column
    private String phone;

    @Column
    private Boolean active;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @OneToMany(mappedBy = "studio")
    @JsonBackReference
    private List<Movie> movies;
}
