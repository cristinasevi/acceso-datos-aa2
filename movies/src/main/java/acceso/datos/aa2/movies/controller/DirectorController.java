package acceso.datos.aa2.movies.controller;

import acceso.datos.aa2.movies.domain.Director;
import acceso.datos.aa2.movies.dto.DirectorDto;
import acceso.datos.aa2.movies.dto.DirectorOutDto;
import acceso.datos.aa2.movies.exception.DirectorNotFoundException;
import acceso.datos.aa2.movies.exception.ErrorResponse;
import acceso.datos.aa2.movies.service.DirectorService;
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
public class DirectorController {

    @Autowired
    private DirectorService directorService;

    @GetMapping("/directors")
    public ResponseEntity<List<DirectorOutDto>> getAll(
            @RequestParam(value = "nationality", required = false) String nationality,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "minAwards", required = false) Integer minAwards) {
        List<DirectorOutDto> directors = directorService.findAll(nationality, active, minAwards);
        return ResponseEntity.ok(directors);
    }

    @GetMapping("/directors/{id}")
    public ResponseEntity<DirectorDto> get(@PathVariable long id) throws DirectorNotFoundException {
        DirectorDto directorDto = directorService.findById(id);
        return ResponseEntity.ok(directorDto);
    }

    @PostMapping("/directors")
    public ResponseEntity<Director> addDirector(@Valid @RequestBody Director director) {
        Director newDirector = directorService.add(director);
        return new ResponseEntity<>(newDirector, HttpStatus.CREATED);
    }

    @PutMapping("/directors/{id}")
    public ResponseEntity<Director> modifyDirector(@PathVariable long id, @RequestBody Director director) throws DirectorNotFoundException {
        Director updatedDirector = directorService.modify(id, director);
        return ResponseEntity.ok(updatedDirector);
    }

    @DeleteMapping("/directors/{id}")
    public ResponseEntity<Void> deleteDirector(@PathVariable long id) throws DirectorNotFoundException {
        directorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DirectorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(DirectorNotFoundException dnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The director does not exist");
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
