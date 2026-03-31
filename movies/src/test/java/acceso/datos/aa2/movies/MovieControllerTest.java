package acceso.datos.aa2.movies;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import acceso.datos.aa2.movies.controller.MovieController;
import acceso.datos.aa2.movies.domain.Movie;
import acceso.datos.aa2.movies.domain.Studio;
import acceso.datos.aa2.movies.dto.MovieOutDto;
import acceso.datos.aa2.movies.dto.MovieUpdateRequest;
import acceso.datos.aa2.movies.exception.MovieNotFoundException;
import acceso.datos.aa2.movies.repository.DirectorRepository;
import acceso.datos.aa2.movies.repository.StudioRepository;
import acceso.datos.aa2.movies.service.MovieService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private StudioRepository studioRepository;

    @MockitoBean
    private DirectorRepository directorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // GET /movies - 200 OK
    @Test
    public void testGetAll200() throws Exception {
        List<MovieOutDto> moviesOutDto = List.of(
                new MovieOutDto(1L, "Catch Me If You Can", "Synopsis", "Action",
                        LocalDate.of(2003, 1, 24), 141, 8.1f, "http://image1.jpg",
                        null, null),
                new MovieOutDto(2L, "The Matrix", "Synopsis", "Science Fiction",
                        LocalDate.of(1999, 3, 31), 136, 8.7f, "http://image2.jpg",
                        null, null)
        );

        when(movieService.findAll(null, null, null, null)).thenReturn(moviesOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/movies")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<MovieOutDto> moviesListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(moviesListResponse);
        assertEquals(2, moviesListResponse.size());
    }

    // GET /movies/{id} - 200 OK
    @Test
    public void testGetById200() throws Exception {
        MovieOutDto movieOutDto = new MovieOutDto(
                1L,
                "Catch Me If You Can",
                "Synopsis",
                "Action",
                LocalDate.of(2003, 1, 24),
                141,
                8.1f,
                "http://image.jpg",
                null,
                null
        );

        when(movieService.findById(1)).thenReturn(movieOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/movies/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        MovieOutDto response = objectMapper.readValue(jsonResponse, MovieOutDto.class);

        assertNotNull(response);
        assertEquals("Catch Me If You Can", response.getTitle());
    }

    // GET /movies/{id} - 404 NOT FOUND
    @Test
    public void testGetById404() throws Exception {
        when(movieService.findById(999L)).thenThrow(new MovieNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/movies/999")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    // POST /movies - 201 CREATED
    @Test
    public void testAddMovie201() throws Exception {
        Movie newMovie = new Movie();
        newMovie.setTitle("New Movie");
        newMovie.setSynopsis("Synopsis");
        newMovie.setReleaseDate(LocalDate.of(2025, 12, 1));
        newMovie.setDuration(120);
        newMovie.setGenre("Action");

        Movie savedMovie = new Movie();
        savedMovie.setId(3L);
        savedMovie.setTitle("New Movie");

        when(movieService.add(any(Movie.class))).thenReturn(savedMovie);

        String movieJson = objectMapper.writeValueAsString(newMovie);

        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson))
                .andExpect(status().isCreated());
    }

    // POST /movies - 400 BAD REQUEST
    @Test
    public void testAddMovie400() throws Exception {
        String invalidMovieJson = "{ \"synopsis\": \"Missing title\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidMovieJson))
                .andExpect(status().isBadRequest());
    }

    // PUT /movies/{id} - 200 OK
    @Test
    public void testModifyMovie200() throws Exception {
        MovieUpdateRequest request = MovieUpdateRequest.builder()
                .title("Updated Movie")
                .synopsis("Updated synopsis")
                .releaseDate(LocalDate.of(2025, 1, 1))
                .duration(120)
                .genre("Action")
                .averageRating(8.5f)
                .studioId(1L)
                .build();

        Studio mockStudio = new Studio();
        mockStudio.setId(1L);
        mockStudio.setName("Warner Bros");

        Movie updatedMovie = new Movie();
        updatedMovie.setId(1L);
        updatedMovie.setTitle("Updated Movie");
        updatedMovie.setStudio(mockStudio);

        when(studioRepository.findById(1L)).thenReturn(Optional.of(mockStudio));
        when(movieService.modify(anyLong(), any(Movie.class))).thenReturn(updatedMovie);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.put("/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    // PUT /movies/{id} - 404 NOT FOUND
    @Test
    public void testModifyMovie404() throws Exception {
        MovieUpdateRequest request = MovieUpdateRequest.builder()
                .title("NonExistent Movie")
                .build();

        when(movieService.modify(anyLong(), any(Movie.class))).thenThrow(new MovieNotFoundException());

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders.put("/movies/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    // DELETE /movies/{id} - 204 NO CONTENT
    @Test
    public void testDeleteMovie204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/movies/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE /movies/{id} - 404 NOT FOUND
    @Test
    public void testDeleteMovie404() throws Exception {
        org.mockito.Mockito.doThrow(new MovieNotFoundException()).when(movieService).delete(999L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/movies/999"))
                .andExpect(status().isNotFound());
    }
}
