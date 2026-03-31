package acceso.datos.aa2.movies;

import acceso.datos.aa2.movies.domain.Review;
import acceso.datos.aa2.movies.dto.ReviewDto;
import acceso.datos.aa2.movies.dto.ReviewOutDto;
import acceso.datos.aa2.movies.exception.ReviewNotFoundException;
import acceso.datos.aa2.movies.repository.ReviewRepository;
import acceso.datos.aa2.movies.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ModelMapper modelMapper;

    // GET /reviews - 200 OK
    @Test
    public void testFindAll() {
        List<Review> mockReviewList = List.of(
                createMockReview(1, "Amazing movie!", 10.0f),
                createMockReview(2, "Great film", 9.0f)
        );

        List<ReviewOutDto> mockReviewOutDtoList = List.of(
                new ReviewOutDto(1L, "Amazing movie!", 10.0f, LocalDate.of(2025, 11, 27),
                        true, false, "csevi", "Catch Me If You Can"),
                new ReviewOutDto(2L, "Great film", 9.0f, LocalDate.of(2025, 11, 28),
                        true, false, "mdiaz", "The Matrix")
        );

        when(reviewRepository.findAll()).thenReturn(mockReviewList);
        when(modelMapper.map(any(List.class), any(Type.class))).thenReturn(mockReviewOutDtoList);

        List<ReviewOutDto> actualReviewList = reviewService.findAll(null, null, null);

        assertEquals(2, actualReviewList.size());
        verify(reviewRepository, times(1)).findAll();
    }

    // GET /movies/{movieId}/reviews - 200 OK
    @Test
    public void testFindByMovieId() {
        List<Review> mockList = List.of(createMockReview(1, "Amazing movie!", 10.0f));
        List<ReviewOutDto> mockOut = List.of(
                new ReviewOutDto(1L, "Amazing movie!", 10.0f, LocalDate.of(2025, 11, 27),
                        true, false, "csevi", "Catch Me If You Can")
        );

        when(reviewRepository.findByMovieId(1L)).thenReturn(mockList);
        when(modelMapper.map(any(List.class), any(Type.class))).thenReturn(mockOut);

        List<ReviewOutDto> result = reviewService.findByMovieId(1L);

        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByMovieId(1L);
    }

    // GET /users/{userId}/reviews - 200 OK
    @Test
    public void testFindByUserId() {
        List<Review> mockList = List.of(createMockReview(1, "Amazing movie!", 10.0f));
        List<ReviewOutDto> mockOut = List.of(
                new ReviewOutDto(1L, "Amazing movie!", 10.0f, LocalDate.of(2025, 11, 27),
                        true, false, "csevi", "Catch Me If You Can")
        );

        when(reviewRepository.findByUserId(1L)).thenReturn(mockList);
        when(modelMapper.map(any(List.class), any(Type.class))).thenReturn(mockOut);

        List<ReviewOutDto> result = reviewService.findByUserId(1L);

        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByUserId(1L);
    }

    // GET /reviews/{id} - 200 OK
    @Test
    public void testFindById() throws ReviewNotFoundException {
        Review mock = createMockReview(1, "Amazing movie!", 10.0f);
        ReviewDto mockDto = new ReviewDto();
        mockDto.setId(1L);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(mock));
        when(modelMapper.map(mock, ReviewDto.class)).thenReturn(mockDto);

        ReviewDto result = reviewService.findById(1L);

        assertNotNull(result);
        verify(reviewRepository, times(1)).findById(1L);
    }

    // PUT /reviews/{id} - 200 OK
    @Test
    public void testModify() throws ReviewNotFoundException {
        Review existing = createMockReview(1, "Amazing movie!", 10.0f);
        Review updated = createMockReview(1, "Updated review", 8.0f);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(reviewRepository.save(any(Review.class))).thenReturn(updated);

        Review result = reviewService.modify(1L, updated);

        assertNotNull(result);
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    // DELETE /reviews/{id} - 204 NO CONTENT
    @Test
    public void testDelete() throws ReviewNotFoundException {
        Review existing = createMockReview(1, "Amazing movie!", 10.0f);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(reviewRepository).delete(any(Review.class));

        reviewService.delete(1L);

        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).delete(any(Review.class));
    }

    private Review createMockReview(long id, String comment, float rating) {
        Review review = new Review();
        review.setId(id);
        review.setComment(comment);
        review.setRating(rating);
        review.setReviewDate(LocalDate.now());
        review.setRecommended(true);
        review.setSpoiler(false);
        return review;
    }
}
