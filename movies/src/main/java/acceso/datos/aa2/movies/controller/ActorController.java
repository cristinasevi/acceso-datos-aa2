package acceso.datos.aa2.movies.controller;

import acceso.datos.aa2.movies.domain.Actor;
import acceso.datos.aa2.movies.dto.ActorDto;
import acceso.datos.aa2.movies.dto.ActorOutDto;
import acceso.datos.aa2.movies.exception.ActorNotFoundException;
import acceso.datos.aa2.movies.exception.ErrorResponse;
import acceso.datos.aa2.movies.service.ActorService;
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
public class ActorController {

    @Autowired
    private ActorService actorService;

    @GetMapping("/actors")
    public ResponseEntity<List<ActorOutDto>> getAll(
            @RequestParam(value = "nationality", required = false) String nationality,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "actorType", required = false) String actorType) {
        List<ActorOutDto> actors = actorService.findAll(nationality, active, actorType);
        return ResponseEntity.ok(actors);
    }

    @GetMapping("/actors/{id}")
    public ResponseEntity<ActorDto> get(@PathVariable long id) throws ActorNotFoundException {
        ActorDto actorDto = actorService.findById(id);
        return ResponseEntity.ok(actorDto);
    }

    @PostMapping("/actors")
    public ResponseEntity<Actor> addActor(@Valid @RequestBody Actor actor) {
        Actor newActor = actorService.add(actor);
        return new ResponseEntity<>(newActor, HttpStatus.CREATED);
    }

    @PutMapping("/actors/{id}")
    public ResponseEntity<Actor> modifyActor(@PathVariable long id, @RequestBody Actor actor) throws ActorNotFoundException {
        Actor updatedActor = actorService.modify(id, actor);
        return ResponseEntity.ok(updatedActor);
    }

    @DeleteMapping("/actors/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable long id) throws ActorNotFoundException {
        actorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ActorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ActorNotFoundException anfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The actor does not exist");
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
