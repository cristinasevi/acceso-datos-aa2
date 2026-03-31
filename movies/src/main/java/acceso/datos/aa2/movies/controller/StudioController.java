package acceso.datos.aa2.movies.controller;

import acceso.datos.aa2.movies.domain.Studio;
import acceso.datos.aa2.movies.dto.StudioDto;
import acceso.datos.aa2.movies.dto.StudioOutDto;
import acceso.datos.aa2.movies.exception.ErrorResponse;
import acceso.datos.aa2.movies.exception.StudioNotFoundException;
import acceso.datos.aa2.movies.service.StudioService;
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
public class StudioController {

    @Autowired
    private StudioService studioService;

    @GetMapping("/studios")
    public ResponseEntity<List<StudioOutDto>> getAll(
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "foundationYear", required = false) Integer foundationYear,
            @RequestParam(value = "active", required = false) Boolean active) {
        List<StudioOutDto> studios = studioService.findAll(country, foundationYear, active);
        return ResponseEntity.ok(studios);
    }

    @GetMapping("/studios/{id}")
    public ResponseEntity<StudioDto> get(@PathVariable long id) throws StudioNotFoundException {
        StudioDto studioDto = studioService.findById(id);
        return ResponseEntity.ok(studioDto);
    }

    @PostMapping("/studios")
    public ResponseEntity<Studio> addStudio(@Valid @RequestBody Studio studio) {
        Studio newStudio = studioService.add(studio);
        return new ResponseEntity<>(newStudio, HttpStatus.CREATED);
    }

    @PutMapping("/studios/{id}")
    public ResponseEntity<Studio> modifyStudio(@PathVariable long id, @RequestBody Studio studio)
            throws StudioNotFoundException {
        Studio updatedStudio = studioService.modify(id, studio);
        return ResponseEntity.ok(updatedStudio);
    }

    @DeleteMapping("/studios/{id}")
    public ResponseEntity<Void> deleteStudio(@PathVariable long id) throws StudioNotFoundException {
        studioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(StudioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(StudioNotFoundException snfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The studio does not exist");
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
