package acceso.datos.aa2.movies.controller;

import acceso.datos.aa2.movies.domain.Review;
import acceso.datos.aa2.movies.dto.ReviewDto;
import acceso.datos.aa2.movies.dto.ReviewInDto;
import acceso.datos.aa2.movies.dto.ReviewOutDto;
import acceso.datos.aa2.movies.exception.ErrorResponse;
import acceso.datos.aa2.movies.exception.MovieNotFoundException;
import acceso.datos.aa2.movies.exception.ReviewNotFoundException;
import acceso.datos.aa2.movies.exception.UserNotFoundException;
import acceso.datos.aa2.movies.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewOutDto>> getAll(
            @RequestParam(value = "minRating", required = false) Integer minRating,
            @RequestParam(value = "recommended", required = false) Boolean recommended,
            @RequestParam(value = "spoiler", required = false) Boolean spoiler) {
        List<ReviewOutDto> reviews = reviewService.findAll(minRating, recommended, spoiler);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/movies/{movieId}/reviews")
    public ResponseEntity<List<ReviewOutDto>> getMovieReviews(@PathVariable long movieId) {
        List<ReviewOutDto> reviews = reviewService.findByMovieId(movieId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<List<ReviewOutDto>> getUserReviews(@PathVariable long userId) {
        List<ReviewOutDto> reviews = reviewService.findByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<ReviewDto> get(@PathVariable long id) throws ReviewNotFoundException {
        ReviewDto reviewDto = reviewService.findById(id);
        return ResponseEntity.ok(reviewDto);
    }

    @PostMapping("/movies/{movieId}/reviews")
    public ResponseEntity<Review> addReview(
            @Valid @RequestBody ReviewInDto reviewInDto,
            @PathVariable long movieId) throws MovieNotFoundException, UserNotFoundException {
        Review newReview = reviewService.add(reviewInDto, movieId);
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<Review> modifyReview(@PathVariable long id, @RequestBody Review review) throws ReviewNotFoundException {
        Review updatedReview = reviewService.modify(id, review);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable long id) throws ReviewNotFoundException {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ReviewNotFoundException rnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The review does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(MovieNotFoundException mnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The movie does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException unfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The user does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException manve) {
        Map<String, String> errors = new HashMap<>();
        manve.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ErrorResponse errorResponse = ErrorResponse.validationError(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
