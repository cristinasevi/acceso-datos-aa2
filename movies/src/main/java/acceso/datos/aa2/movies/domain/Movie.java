package acceso.datos.aa2.movies.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Entity(name = "movies")

public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull(message = "Title is required")
    @NotBlank(message = "Title cannot be empty")
    private String title;

    @Column
    @Size(max = 1000, message = "Synopsis cannot exceed 1000 characters")
    private String synopsis;

    @Column(name = "release_date")
    @NotNull(message = "Release date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Column
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 600, message = "Duration cannot exceed 600 minutes")
    private Integer duration;

    @Column(length = 50)
    @Pattern(regexp = "^(Action|Drama|Comedy|Horror|Science Fiction|Romance|Thriller|Animation|Documentary|Adventure)$",
            message = "Invalid genre")
    private String genre;

    @Column(name = "average_rating")
    @Min(value = 0, message = "Rating must be between 0 and 10")
    @Max(value = 10, message = "Rating must be between 0 and 10")
    private Float averageRating;

    @Column(name = "image_url")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "studio_id")
    private Studio studio;

    @ManyToOne
    @JoinColumn(name = "director_id")
    private Director director;

    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors;

    @OneToMany(mappedBy = "movie")
    @JsonBackReference
    private List<Review> reviews;
}
