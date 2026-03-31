package acceso.datos.aa2.movies.service;

import acceso.datos.aa2.movies.domain.Movie;
import acceso.datos.aa2.movies.domain.Review;
import acceso.datos.aa2.movies.domain.User;
import acceso.datos.aa2.movies.dto.ReviewDto;
import acceso.datos.aa2.movies.dto.ReviewInDto;
import acceso.datos.aa2.movies.dto.ReviewOutDto;
import acceso.datos.aa2.movies.exception.MovieNotFoundException;
import acceso.datos.aa2.movies.exception.ReviewNotFoundException;
import acceso.datos.aa2.movies.exception.UserNotFoundException;
import acceso.datos.aa2.movies.repository.MovieRepository;
import acceso.datos.aa2.movies.repository.ReviewRepository;
import acceso.datos.aa2.movies.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Review add(ReviewInDto reviewInDto, long movieId) throws MovieNotFoundException, UserNotFoundException {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(MovieNotFoundException::new);

        User user = userRepository.findById(reviewInDto.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Review review = new Review();
        modelMapper.map(reviewInDto, review);
        review.setMovie(movie);
        review.setUser(user);

        if (review.getReviewDate() == null) {
            review.setReviewDate(LocalDate.now());
        }

        return reviewRepository.save(review);
    }

    public void delete(long id) throws ReviewNotFoundException {
        Review review = reviewRepository.findById(id)
                .orElseThrow(ReviewNotFoundException::new);
        reviewRepository.delete(review);
    }

    public List<ReviewOutDto> findAll(Integer minRating, Boolean recommended, Boolean spoiler) {
        List<Review> reviews;

        boolean hasMinRating = minRating != null;
        boolean hasRecommended = recommended != null;
        boolean hasSpoiler = spoiler != null;

        if (hasMinRating && hasRecommended && hasSpoiler) {
            // 3 filtros
            reviews = reviewRepository.findByRatingGreaterThanEqualAndRecommendedAndSpoiler(minRating, recommended, spoiler);
        } else if (hasMinRating && hasRecommended) {
            // minRating + recommended
            reviews = reviewRepository.findByRatingGreaterThanEqualAndRecommended(minRating, recommended);
        } else if (hasMinRating && hasSpoiler) {
            // minRating + spoiler
            reviews = reviewRepository.findByRatingGreaterThanEqualAndSpoiler(minRating, spoiler);
        } else if (hasRecommended && hasSpoiler) {
            // recommended + spoiler
            reviews = reviewRepository.findByRecommendedAndSpoiler(recommended, spoiler);
        } else if (hasMinRating) {
            // Solo minRating
            reviews = reviewRepository.findByRatingGreaterThanEqual(minRating);
        } else if (hasRecommended) {
            // Solo recommended
            reviews = reviewRepository.findByRecommended(recommended);
        } else if (hasSpoiler) {
            // Solo spoiler
            reviews = reviewRepository.findBySpoiler(spoiler);
        } else {
            // Sin filtros
            reviews = reviewRepository.findAll();
        }

        return mapToOutDto(reviews);
    }

    public List<ReviewOutDto> findByMovieId(long movieId) {
        List<Review> reviews = reviewRepository.findByMovieId(movieId);
        return mapToOutDto(reviews);
    }

    public List<ReviewOutDto> findByUserId(long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return mapToOutDto(reviews);
    }

    public ReviewDto findById(long id) throws ReviewNotFoundException {
        Review review = reviewRepository.findById(id)
                .orElseThrow(ReviewNotFoundException::new);

        ReviewDto reviewDto = modelMapper.map(review, ReviewDto.class);

        // Relaciones simplificadas
        if (review.getUser() != null) {
            reviewDto.setUserId(review.getUser().getId());
            reviewDto.setUsername(review.getUser().getUsername());
        }
        if (review.getMovie() != null) {
            reviewDto.setMovieId(review.getMovie().getId());
            reviewDto.setMovieTitle(review.getMovie().getTitle());
        }

        return reviewDto;
    }

    public Review modify(long id, Review review) throws ReviewNotFoundException {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(ReviewNotFoundException::new);

        existingReview.setComment(review.getComment());
        existingReview.setRating(review.getRating());
        existingReview.setReviewDate(review.getReviewDate());
        existingReview.setRecommended(review.getRecommended());
        existingReview.setSpoiler(review.getSpoiler());

        return reviewRepository.save(existingReview);
    }

    // Método auxiliar para mapear a ReviewOutDto con relaciones
    private List<ReviewOutDto> mapToOutDto(List<Review> reviews) {
        List<ReviewOutDto> reviewOutDtos = modelMapper.map(reviews, new TypeToken<List<ReviewOutDto>>() {}.getType());

        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);
            ReviewOutDto dto = reviewOutDtos.get(i);

            if (review.getUser() != null) {
                dto.setUsername(review.getUser().getUsername());
            }
            if (review.getMovie() != null) {
                dto.setMovieTitle(review.getMovie().getTitle());
            }
        }

        return reviewOutDtos;
    }
}
