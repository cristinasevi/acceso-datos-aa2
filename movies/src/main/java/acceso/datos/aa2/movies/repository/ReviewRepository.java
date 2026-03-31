package acceso.datos.aa2.movies.repository;

import acceso.datos.aa2.movies.domain.Movie;
import acceso.datos.aa2.movies.domain.Review;
import acceso.datos.aa2.movies.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Long> {

    List<Review> findAll();

    List<Review> findByMovie(Movie movie);

    List<Review> findByUser(User user);

    List<Review> findByMovieId(Long movieId);

    List<Review> findByUserId(Long userId);

    List<Review> findByRatingGreaterThanEqual(Integer minRating);

    List<Review> findByRecommended(Boolean recommended);

    List<Review> findBySpoiler(Boolean spoiler);

    // Combinaciones de 2 filtros
    List<Review> findByRatingGreaterThanEqualAndRecommended(Integer minRating, Boolean recommended);

    List<Review> findByRatingGreaterThanEqualAndSpoiler(Integer minRating, Boolean spoiler);

    List<Review> findByRecommendedAndSpoiler(Boolean recommended, Boolean spoiler);

    // Combinación de 3 filtros
    List<Review> findByRatingGreaterThanEqualAndRecommendedAndSpoiler(Integer minRating, Boolean recommended, Boolean spoiler);
}
