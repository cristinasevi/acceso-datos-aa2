package acceso.datos.aa2.movies;

import acceso.datos.aa2.movies.domain.Actor;
import acceso.datos.aa2.movies.dto.ActorDto;
import acceso.datos.aa2.movies.dto.ActorOutDto;
import acceso.datos.aa2.movies.exception.ActorNotFoundException;
import acceso.datos.aa2.movies.repository.ActorRepository;
import acceso.datos.aa2.movies.service.ActorService;
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
public class ActorServiceTest {

    @InjectMocks
    private ActorService actorService;

    @Mock
    private ActorRepository actorRepository;

    @Mock
    private ModelMapper modelMapper;

    // GET /actors - 200 OK
    @Test
    public void testFindAll() {
        List<Actor> mockActorList = List.of(
                createMockActor(1, "Leonardo DiCaprio", "American"),
                createMockActor(2, "Tom Hanks", "American")
        );
        List<ActorOutDto> modelMapperOut = List.of(
                new ActorOutDto(1L, "Leonardo DiCaprio", "American", true, "Lead"),
                new ActorOutDto(2L, "Tom Hanks", "American", true, "Lead")
        );

        when(actorRepository.findAll()).thenReturn(mockActorList);
        when(modelMapper.map(mockActorList, new TypeToken<List<ActorOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<ActorOutDto> actualActorList = actorService.findAll(null, null, null);

        assertEquals(2, actualActorList.size());
        verify(actorRepository, times(1)).findAll();
    }

    // GET /actors/{id} - 200 OK
    @Test
    public void testFindById() throws ActorNotFoundException {
        Actor mockActor = createMockActor(1, "Leonardo DiCaprio", "American");
        ActorDto mockActorDto = new ActorDto();
        mockActorDto.setId(1L);
        mockActorDto.setName("Leonardo DiCaprio");

        when(actorRepository.findById(1L)).thenReturn(Optional.of(mockActor));
        when(modelMapper.map(mockActor, ActorDto.class)).thenReturn(mockActorDto);

        ActorDto result = actorService.findById(1L);

        assertNotNull(result);
        verify(actorRepository, times(1)).findById(1L);
    }

    // POST /actors - 201 CREATED
    @Test
    public void testAdd() {
        Actor newActor = createMockActor(0, "Brad Pitt", "American");
        Actor savedActor = createMockActor(3, "Brad Pitt", "American");

        when(actorRepository.save(any(Actor.class))).thenReturn(savedActor);

        Actor result = actorService.add(newActor);

        assertNotNull(result);
        verify(actorRepository, times(1)).save(any(Actor.class));
    }

    // PUT /actors/{id} - 200 OK
    @Test
    public void testModify() throws ActorNotFoundException {
        Actor existingActor = createMockActor(1, "Leonardo DiCaprio", "American");
        Actor updatedActor = createMockActor(1, "Leonardo DiCaprio Updated", "American");

        when(actorRepository.findById(1L)).thenReturn(Optional.of(existingActor));
        when(actorRepository.save(any(Actor.class))).thenReturn(updatedActor);

        Actor result = actorService.modify(1L, updatedActor);

        assertNotNull(result);
        verify(actorRepository, times(1)).findById(1L);
        verify(actorRepository, times(1)).save(any(Actor.class));
    }

    // DELETE /actors/{id} - 204 NO CONTENT
    @Test
    public void testDelete() throws ActorNotFoundException {
        Actor existingActor = createMockActor(1, "Leonardo DiCaprio", "American");

        when(actorRepository.findById(1L)).thenReturn(Optional.of(existingActor));
        doNothing().when(actorRepository).delete(any(Actor.class));

        actorService.delete(1L);

        verify(actorRepository, times(1)).findById(1L);
        verify(actorRepository, times(1)).delete(any(Actor.class));
    }

    private Actor createMockActor(long id, String name, String nationality) {
        Actor actor = new Actor();
        actor.setId(id);
        actor.setName(name);
        actor.setNationality(nationality);
        actor.setBirthDate(LocalDate.of(1974, 11, 11));
        actor.setActive(true);
        actor.setAwards(1);
        actor.setActorType("Lead");
        return actor;
    }
}
