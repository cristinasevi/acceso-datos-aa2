package acceso.datos.aa2.movies.service;

import acceso.datos.aa2.movies.domain.Movie;
import acceso.datos.aa2.movies.dto.*;
import acceso.datos.aa2.movies.exception.MovieNotFoundException;
import acceso.datos.aa2.movies.repository.MovieRepository;
import acceso.datos.aa2.movies.util.DateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Movie add(Movie movie) {
        return movieRepository.save(movie);
    }

    public void delete(long id) throws MovieNotFoundException {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(MovieNotFoundException::new);
        movieRepository.delete(movie);
    }

    public List<MovieOutDto> findAll(String genre, LocalDate releaseDateFrom, LocalDate releaseDateTo, Float minRating) {
        List<Movie> movies;

        boolean hasGenre = genre != null && !genre.isEmpty();
        boolean hasFrom = releaseDateFrom != null;
        boolean hasTo = releaseDateTo != null;
        boolean hasRating = minRating != null;

        // Rango de fechas completo (from y to)
        boolean hasDateRange = hasFrom && hasTo;

        if (hasGenre && hasRating && hasDateRange) {
            // 3 filtros: genre + rating + rango fechas
            movies = movieRepository.findByGenreAndAverageRatingGreaterThanEqualAndReleaseDateBetween(
                    genre, minRating, releaseDateFrom, releaseDateTo);
        } else if (hasGenre && hasRating && hasFrom) {
            // genre + rating + desde
            movies = movieRepository.findByGenreAndAverageRatingGreaterThanEqualAndReleaseDateGreaterThanEqual(
                    genre, minRating, releaseDateFrom);
        } else if (hasGenre && hasRating && hasTo) {
            // genre + rating + hasta
            movies = movieRepository.findByGenreAndAverageRatingGreaterThanEqualAndReleaseDateLessThanEqual(
                    genre, minRating, releaseDateTo);
        } else if (hasGenre && hasDateRange) {
            // genre + rango fechas
            movies = movieRepository.findByGenreAndReleaseDateBetween(genre, releaseDateFrom, releaseDateTo);
        } else if (hasGenre && hasFrom) {
            // genre + desde
            movies = movieRepository.findByGenreAndReleaseDateGreaterThanEqual(genre, releaseDateFrom);
        } else if (hasGenre && hasTo) {
            // genre + hasta
            movies = movieRepository.findByGenreAndReleaseDateLessThanEqual(genre, releaseDateTo);
        } else if (hasGenre && hasRating) {
            // genre + rating
            movies = movieRepository.findByGenreAndAverageRatingGreaterThanEqual(genre, minRating);
        } else if (hasRating && hasDateRange) {
            // rating + rango fechas
            movies = movieRepository.findByAverageRatingGreaterThanEqualAndReleaseDateBetween(
                    minRating, releaseDateFrom, releaseDateTo);
        } else if (hasRating && hasFrom) {
            // rating + desde
            movies = movieRepository.findByAverageRatingGreaterThanEqualAndReleaseDateGreaterThanEqual(
                    minRating, releaseDateFrom);
        } else if (hasRating && hasTo) {
            // rating + hasta
            movies = movieRepository.findByAverageRatingGreaterThanEqualAndReleaseDateLessThanEqual(
                    minRating, releaseDateTo);
        } else if (hasGenre) {
            // Solo genre
            movies = movieRepository.findByGenre(genre);
        } else if (hasRating) {
            // Solo rating
            movies = movieRepository.findByAverageRatingGreaterThanEqual(minRating);
        } else if (hasDateRange) {
            // Solo rango fechas
            movies = movieRepository.findByReleaseDateBetween(releaseDateFrom, releaseDateTo);
        } else if (hasFrom) {
            // Solo desde
            movies = movieRepository.findByReleaseDateGreaterThanEqual(releaseDateFrom);
        } else if (hasTo) {
            // Solo hasta
            movies = movieRepository.findByReleaseDateLessThanEqual(releaseDateTo);
        } else {
            // Sin filtros
            movies = movieRepository.findAll();
        }

        List<MovieOutDto> movieOutDtos = modelMapper.map(movies, new TypeToken<List<MovieOutDto>>() {}.getType());

        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            MovieOutDto dto = movieOutDtos.get(i);

            if (movie.getStudio() != null) {
                dto.setStudio(modelMapper.map(movie.getStudio(), StudioOutDto.class));
            }

            if (movie.getDirector() != null) {
                dto.setDirector(modelMapper.map(movie.getDirector(), DirectorOutDto.class));
            }
        }

        return movieOutDtos;
    }

    public MovieOutDto findById(long id) throws MovieNotFoundException {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(MovieNotFoundException::new);

        MovieOutDto movieOutDto = modelMapper.map(movie, MovieOutDto.class);

        if (movie.getStudio() != null) {
            movieOutDto.setStudio(modelMapper.map(movie.getStudio(), StudioOutDto.class));
        }

        if (movie.getDirector() != null) {
            movieOutDto.setDirector(modelMapper.map(movie.getDirector(), DirectorOutDto.class));
        }

        return movieOutDto;
    }

    public MovieOutDtoV2 findByIdV2(long id) throws MovieNotFoundException {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(MovieNotFoundException::new);

        MovieOutDtoV2 dto = modelMapper.map(movie, MovieOutDtoV2.class);

        if (movie.getReleaseDate() != null) {
            dto.setDaysUntilRelease(DateUtil.getDaysBetweenDates(LocalDate.now(), movie.getReleaseDate()));
        }

        if (movie.getDirector() != null) {
            dto.setDirectorName(movie.getDirector().getName());
        }

        if (movie.getStudio() != null) {
            dto.setStudio(modelMapper.map(movie.getStudio(), StudioOutDto.class));
        }

        if (movie.getDirector() != null) {
            dto.setDirector(modelMapper.map(movie.getDirector(), DirectorOutDto.class));
        }

        return dto;
    }

    public Movie modify(long id, Movie movie) throws MovieNotFoundException {
        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(MovieNotFoundException::new);

        existingMovie.setTitle(movie.getTitle());
        existingMovie.setSynopsis(movie.getSynopsis());
        existingMovie.setReleaseDate(movie.getReleaseDate());
        existingMovie.setDuration(movie.getDuration());
        existingMovie.setGenre(movie.getGenre());
        existingMovie.setImageUrl(movie.getImageUrl());
        existingMovie.setAverageRating(movie.getAverageRating());

        if (movie.getImageUrl() != null) {
            existingMovie.setImageUrl(movie.getImageUrl());
        }

        if (movie.getStudio() != null) {
            existingMovie.setStudio(movie.getStudio());
        }

        if (movie.getDirector() != null) {
            existingMovie.setDirector(movie.getDirector());
        }

        return movieRepository.save(existingMovie);
    }

    public Movie modifyPartial(long id, MovieUpdateRequest request) throws MovieNotFoundException {
        Movie existing = movieRepository.findById(id)
                .orElseThrow(MovieNotFoundException::new);

        // Solo actualiza si el campo viene en el request (no null)
        if (request.getTitle() != null) existing.setTitle(request.getTitle());
        if (request.getSynopsis() != null) existing.setSynopsis(request.getSynopsis());
        if (request.getReleaseDate() != null) existing.setReleaseDate(request.getReleaseDate());
        if (request.getDuration() != null) existing.setDuration(request.getDuration());
        if (request.getGenre() != null) existing.setGenre(request.getGenre());
        if (request.getImageUrl() != null) existing.setImageUrl(request.getImageUrl());
        if (request.getAverageRating() != null) existing.setAverageRating(request.getAverageRating());

        return movieRepository.save(existing);
    }

    public void softDelete(long id) throws MovieNotFoundException {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(MovieNotFoundException::new);
        movie.setActive(false);
        movieRepository.save(movie);
    }
}
