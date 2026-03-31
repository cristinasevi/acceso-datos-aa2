package acceso.datos.aa2.movies;

import acceso.datos.aa2.movies.domain.Movie;
import acceso.datos.aa2.movies.dto.MovieOutDto;
import acceso.datos.aa2.movies.exception.MovieNotFoundException;
import acceso.datos.aa2.movies.repository.MovieRepository;
import acceso.datos.aa2.movies.service.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @InjectMocks
    private MovieService movieService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ModelMapper modelMapper;

    // GET /movies - 200 OK
    @Test
    public void testFindAll() {
        List<Movie> mockList = List.of(createMockMovie(1, "Catch Me If You Can", "Action"));
        List<MovieOutDto> mockOut = List.of(
                new MovieOutDto(1L, "Catch Me If You Can", "Synopsis", "Action",
                        LocalDate.of(2003, 1, 24), 141, 8.1f, "http://image.jpg",
                        null, null)
        );

        when(movieRepository.findAll()).thenReturn(mockList);
        when(modelMapper.map(mockList, new TypeToken<List<MovieOutDto>>() {}.getType())).thenReturn(mockOut);

        List<MovieOutDto> result = movieService.findAll(null, null, null, null);

        assertEquals(1, result.size());
        verify(movieRepository, times(1)).findAll();
    }

    // GET /movies/{id} - 200 OK
    @Test
    public void testFindById() throws MovieNotFoundException {
        Movie mockMovie = createMockMovie(1, "Catch Me If You Can", "Action");
        MovieOutDto mockOut = new MovieOutDto(
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

        when(movieRepository.findById(1L)).thenReturn(Optional.of(mockMovie));
        when(modelMapper.map(mockMovie, MovieOutDto.class)).thenReturn(mockOut);

        MovieOutDto result = movieService.findById(1);

        assertNotNull(result);
        assertEquals("Catch Me If You Can", result.getTitle());
        verify(movieRepository, times(1)).findById(1L);
    }

    // POST /movies - 201 CREATED
    @Test
    public void testAdd() {
        Movie newMovie = createMockMovie(0, "New Movie", "Action");
        Movie saved = createMockMovie(3, "New Movie", "Action");

        when(movieRepository.save(any(Movie.class))).thenReturn(saved);

        Movie result = movieService.add(newMovie);

        assertNotNull(result);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    // PUT /movies/{id} - 200 OK
    @Test
    public void testModify() throws MovieNotFoundException {
        Movie existing = createMockMovie(1, "Catch Me If You Can", "Action");
        Movie updated = createMockMovie(1, "Updated Movie", "Action");

        when(movieRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(movieRepository.save(any(Movie.class))).thenReturn(updated);

        Movie result = movieService.modify(1L, updated);

        assertNotNull(result);
        verify(movieRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    // DELETE /movies/{id} - 204 NO CONTENT
    @Test
    public void testDelete() throws MovieNotFoundException {
        Movie existing = createMockMovie(1, "Catch Me If You Can", "Action");

        when(movieRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(movieRepository).delete(any(Movie.class));

        movieService.delete(1L);

        verify(movieRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).delete(any(Movie.class));
    }

    private Movie createMockMovie(long id, String title, String genre) {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle(title);
        movie.setSynopsis("Synopsis for " + title);
        movie.setGenre(genre);
        movie.setReleaseDate(LocalDate.of(2003, 1, 24));
        movie.setDuration(141);
        movie.setAverageRating(8.1f);
        movie.setImageUrl("http://image.jpg");
        return movie;
    }
}
