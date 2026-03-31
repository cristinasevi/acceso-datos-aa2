package acceso.datos.aa2.movies;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import acceso.datos.aa2.movies.controller.ReviewController;
import acceso.datos.aa2.movies.domain.Review;
import acceso.datos.aa2.movies.dto.ReviewDto;
import acceso.datos.aa2.movies.dto.ReviewInDto;
import acceso.datos.aa2.movies.dto.ReviewOutDto;
import acceso.datos.aa2.movies.exception.ReviewNotFoundException;
import acceso.datos.aa2.movies.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    // GET /reviews - 200 OK
    @Test
    public void testGetAll200() throws Exception {
        List<ReviewOutDto> reviewsOutDto = List.of(
                new ReviewOutDto(1L, "Amazing movie!", 10.0f, LocalDate.of(2025, 11, 27), true, false, "csevi", "Catch Me If You Can"),
                new ReviewOutDto(2L, "Great film", 9.0f, LocalDate.of(2025, 11, 28), true, false, "mdiaz", "The Matrix")
        );

        when(reviewService.findAll(null, null, null)).thenReturn(reviewsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/reviews")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<ReviewOutDto> reviewsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(reviewsListResponse);
        assertEquals(2, reviewsListResponse.size());
    }

    // GET /movies/{movieId}/reviews - 200 OK
    @Test
    public void testGetMovieReviews200() throws Exception {
        List<ReviewOutDto> reviewsOutDto = List.of(
                new ReviewOutDto(1L, "Amazing movie!", 10.0f, LocalDate.of(2025, 11, 27), true, false, "csevi", "Catch Me If You Can")
        );

        when(reviewService.findByMovieId(1L)).thenReturn(reviewsOutDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/movies/1/reviews")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    // GET /users/{userId}/reviews - 200 OK
    @Test
    public void testGetUserReviews200() throws Exception {
        List<ReviewOutDto> reviewsOutDto = List.of(
                new ReviewOutDto(1L, "Amazing movie!", 10.0f, LocalDate.of(2025, 11, 27), true, false, "csevi", "Catch Me If You Can")
        );

        when(reviewService.findByUserId(1L)).thenReturn(reviewsOutDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1/reviews")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    // GET /reviews/{id} - 200 OK
    @Test
    public void testGetById200() throws Exception {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(1L);
        reviewDto.setComment("Amazing movie!");

        when(reviewService.findById(1L)).thenReturn(reviewDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/reviews/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    // GET /reviews/{id} - 404 NOT FOUND
    @Test
    public void testGetById404() throws Exception {
        when(reviewService.findById(999L)).thenThrow(new ReviewNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/reviews/999")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    // POST /movies/{movieId}/reviews - 201 CREATED
    @Test
    public void testAddReview201() throws Exception {
        ReviewInDto newReviewDto = new ReviewInDto();
        newReviewDto.setComment("Great movie!");
        newReviewDto.setRating(9.0f);
        newReviewDto.setRecommended(true);
        newReviewDto.setSpoiler(false);
        newReviewDto.setUserId(1L);

        Review savedReview = new Review();
        savedReview.setId(3L);
        savedReview.setComment("Great movie!");

        when(reviewService.add(any(ReviewInDto.class), anyLong())).thenReturn(savedReview);

        String reviewJson = objectMapper.writeValueAsString(newReviewDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/movies/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson))
                .andExpect(status().isCreated());
    }

    // POST /movies/{movieId}/reviews - 400 BAD REQUEST
    @Test
    public void testAddReview400() throws Exception {
        String invalidReviewJson = "{ \"rating\": 9 }"; // Falta "comment"

        mockMvc.perform(MockMvcRequestBuilders.post("/movies/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidReviewJson))
                .andExpect(status().isBadRequest());
    }

    // PUT /reviews/{id} - 200 OK
    @Test
    public void testModifyReview200() throws Exception {
        Review updatedReview = new Review();
        updatedReview.setId(1L);
        updatedReview.setComment("Updated review");

        when(reviewService.modify(anyLong(), any(Review.class))).thenReturn(updatedReview);

        String reviewJson = objectMapper.writeValueAsString(updatedReview);

        mockMvc.perform(MockMvcRequestBuilders.put("/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson))
                .andExpect(status().isOk());
    }

    // PUT /reviews/{id} - 404 NOT FOUND
    @Test
    public void testModifyReview404() throws Exception {
        Review updatedReview = new Review();
        updatedReview.setComment("NonExistent review");

        when(reviewService.modify(anyLong(), any(Review.class))).thenThrow(new ReviewNotFoundException());

        String reviewJson = objectMapper.writeValueAsString(updatedReview);

        mockMvc.perform(MockMvcRequestBuilders.put("/reviews/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson))
                .andExpect(status().isNotFound());
    }

    // DELETE /reviews/{id} - 204 NO CONTENT
    @Test
    public void testDeleteReview204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/reviews/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE /reviews/{id} - 404 NOT FOUND
    @Test
    public void testDeleteReview404() throws Exception {
        org.mockito.Mockito.doThrow(new ReviewNotFoundException()).when(reviewService).delete(999L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/reviews/999"))
                .andExpect(status().isNotFound());
    }
}
