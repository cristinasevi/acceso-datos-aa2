package acceso.datos.aa2.movies.repository;

import acceso.datos.aa2.movies.domain.Movie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {

    List<Movie> findAll();

    List<Movie> findByGenre(String genre);

    List<Movie> findByReleaseDateBetween(LocalDate from, LocalDate to);

    List<Movie> findByReleaseDateGreaterThanEqual(LocalDate from);

    List<Movie> findByReleaseDateLessThanEqual(LocalDate to);

    List<Movie> findByAverageRatingGreaterThanEqual(Float minRating);

    List<Movie> findByDirectorId(Long directorId);

    List<Movie> findByStudioId(Long studioId);

    // Combinaciones con genre
    List<Movie> findByGenreAndAverageRatingGreaterThanEqual(String genre, Float minRating);

    List<Movie> findByGenreAndReleaseDateBetween(String genre, LocalDate from, LocalDate to);

    List<Movie> findByGenreAndReleaseDateGreaterThanEqual(String genre, LocalDate from);

    List<Movie> findByGenreAndReleaseDateLessThanEqual(String genre, LocalDate to);

    // Combinaciones con rating y fechas
    List<Movie> findByAverageRatingGreaterThanEqualAndReleaseDateBetween(Float minRating, LocalDate from, LocalDate to);

    List<Movie> findByAverageRatingGreaterThanEqualAndReleaseDateGreaterThanEqual(Float minRating, LocalDate from);

    List<Movie> findByAverageRatingGreaterThanEqualAndReleaseDateLessThanEqual(Float minRating, LocalDate to);

    // Combinación de 3 filtros (genre + rating + fechas)
    List<Movie> findByGenreAndAverageRatingGreaterThanEqualAndReleaseDateBetween(String genre, Float minRating, LocalDate from, LocalDate to);

    List<Movie> findByGenreAndAverageRatingGreaterThanEqualAndReleaseDateGreaterThanEqual(String genre, Float minRating, LocalDate from);

    List<Movie> findByGenreAndAverageRatingGreaterThanEqualAndReleaseDateLessThanEqual(String genre, Float minRating, LocalDate to);
}
