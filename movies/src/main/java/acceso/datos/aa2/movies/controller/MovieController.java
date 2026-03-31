package acceso.datos.aa2.movies.controller;

import acceso.datos.aa2.movies.domain.Director;
import acceso.datos.aa2.movies.domain.Movie;
import acceso.datos.aa2.movies.domain.Studio;
import acceso.datos.aa2.movies.dto.*;
import acceso.datos.aa2.movies.exception.DirectorNotFoundException;
import acceso.datos.aa2.movies.exception.ErrorResponse;
import acceso.datos.aa2.movies.exception.MovieNotFoundException;
import acceso.datos.aa2.movies.exception.StudioNotFoundException;
import acceso.datos.aa2.movies.repository.DirectorRepository;
import acceso.datos.aa2.movies.repository.StudioRepository;
import acceso.datos.aa2.movies.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private StudioRepository studioRepository;

    @Autowired
    private DirectorRepository directorRepository;

    @GetMapping("/v1/movies")
    public ResponseEntity<List<MovieOutDto>> getAll(
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "releaseDateFrom", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateFrom,
            @RequestParam(value = "releaseDateTo", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateTo,
            @RequestParam(value = "minRating", required = false) Float minRating) {
        List<MovieOutDto> movies = movieService.findAll(genre, releaseDateFrom, releaseDateTo, minRating);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/v1/movies/{id}")
    public ResponseEntity<MovieOutDto> get(@PathVariable long id) throws MovieNotFoundException {
        MovieOutDto movieOutDto = movieService.findById(id);
        return ResponseEntity.ok(movieOutDto);
    }

    @GetMapping("/v2/movies/{id}")
    public ResponseEntity<MovieOutDtoV2> getV2(@PathVariable long id) throws MovieNotFoundException {
        return ResponseEntity.ok(movieService.findByIdV2(id));
    }

    @PostMapping("/v1/movies")
    @Deprecated
    public ResponseEntity<Movie> addMovie(@Valid @RequestBody MovieCreateRequest movieRequest) {
        Movie movie = new Movie();
        movie.setTitle(movieRequest.getTitle());
        movie.setSynopsis(movieRequest.getSynopsis());
        movie.setReleaseDate(movieRequest.getReleaseDate());
        movie.setDuration(movieRequest.getDuration());
        movie.setGenre(movieRequest.getGenre());
        movie.setImageUrl(movieRequest.getImageUrl());
        movie.setAverageRating(movieRequest.getAverageRating());

        if (movieRequest.getStudioId() != null && movieRequest.getStudioId() > 0) {
            try {
                Studio studio = studioRepository.findById(movieRequest.getStudioId())
                        .orElseThrow(StudioNotFoundException::new);
                movie.setStudio(studio);
            } catch (StudioNotFoundException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        if (movieRequest.getDirectorId() != null && movieRequest.getDirectorId() > 0) {
            try {
                Director director = directorRepository.findById(movieRequest.getDirectorId())
                        .orElseThrow(DirectorNotFoundException::new);
                movie.setDirector(director);
            } catch (DirectorNotFoundException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        Movie newMovie = movieService.add(movie);
        return new ResponseEntity<>(newMovie, HttpStatus.CREATED);
    }

    @PostMapping("/v2/movies")
    public ResponseEntity<Movie> addMovieV2(@Valid @RequestBody MovieCreateRequestV2 movieRequest) {
        Movie movie = new Movie();
        movie.setTitle(movieRequest.getTitle());
        movie.setSynopsis(movieRequest.getSynopsis());
        movie.setReleaseDate(movieRequest.getReleaseDate());
        movie.setDuration(movieRequest.getDuration());
        movie.setGenre(movieRequest.getGenre());
        movie.setImageUrl(movieRequest.getImageUrl());
        movie.setAverageRating(movieRequest.getAverageRating());

        if (movieRequest.getStudio() != null && movieRequest.getStudio().getId() != null) {
            try {
                movie.setStudio(studioRepository.findById(movieRequest.getStudio().getId())
                        .orElseThrow(StudioNotFoundException::new));
            } catch (StudioNotFoundException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        if (movieRequest.getDirector() != null && movieRequest.getDirector().getId() != null) {
            try {
                movie.setDirector(directorRepository.findById(movieRequest.getDirector().getId())
                        .orElseThrow(DirectorNotFoundException::new));
            } catch (DirectorNotFoundException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        return new ResponseEntity<>(movieService.add(movie), HttpStatus.CREATED);
    }

    @PutMapping("/v1/movies/{id}")
    public ResponseEntity<Movie> modifyMovie(@PathVariable long id, @RequestBody MovieUpdateRequest movieUpdateRequest) throws MovieNotFoundException {
        try {
            Movie movie = new Movie();
            movie.setTitle(movieUpdateRequest.getTitle());
            movie.setSynopsis(movieUpdateRequest.getSynopsis());
            movie.setReleaseDate(movieUpdateRequest.getReleaseDate());
            movie.setDuration(movieUpdateRequest.getDuration());
            movie.setGenre(movieUpdateRequest.getGenre());
            movie.setImageUrl(movieUpdateRequest.getImageUrl());
            movie.setAverageRating(movieUpdateRequest.getAverageRating());

            if (movieUpdateRequest.getStudioId() != null) {
                Studio studio = studioRepository.findById(movieUpdateRequest.getStudioId())
                        .orElseThrow(StudioNotFoundException::new);
                movie.setStudio(studio);
            }
            
            if (movieUpdateRequest.getDirectorId() != null) {
                Director director = directorRepository.findById(movieUpdateRequest.getDirectorId())
                        .orElseThrow(DirectorNotFoundException::new);
                movie.setDirector(director);
            }

            Movie updatedMovie = movieService.modify(id, movie);
            return ResponseEntity.ok(updatedMovie);
        } catch (StudioNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (DirectorNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/v2/movies/{id}")
    public ResponseEntity<Movie> modifyMovieV2(
            @PathVariable long id,
            @RequestBody MovieUpdateRequest movieUpdateRequest) throws MovieNotFoundException {
        Movie updatedMovie = movieService.modifyPartial(id, movieUpdateRequest);
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/v1/movies/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable long id) throws MovieNotFoundException {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v2/movies/{id}")
    public ResponseEntity<Void> deleteMovieV2(@PathVariable long id) throws MovieNotFoundException {
        movieService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(MovieNotFoundException mnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The movie does not exist");
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
