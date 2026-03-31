package acceso.datos.aa2.movies;

import acceso.datos.aa2.movies.domain.Director;
import acceso.datos.aa2.movies.dto.DirectorDto;
import acceso.datos.aa2.movies.dto.DirectorOutDto;
import acceso.datos.aa2.movies.exception.DirectorNotFoundException;
import acceso.datos.aa2.movies.repository.DirectorRepository;
import acceso.datos.aa2.movies.service.DirectorService;
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
public class DirectorServiceTest {

    @InjectMocks
    private DirectorService directorService;

    @Mock
    private DirectorRepository directorRepository;

    @Mock
    private ModelMapper modelMapper;

    // GET /directors - 200 OK
    @Test
    public void testFindAll() {
        List<Director> mockList = List.of(createMockDirector(1, "Christopher Nolan", "British"));
        List<DirectorOutDto> mockOut = List.of(new DirectorOutDto(1L, "Christopher Nolan", "British", true));

        when(directorRepository.findAll()).thenReturn(mockList);
        when(modelMapper.map(mockList, new TypeToken<List<DirectorOutDto>>() {}.getType())).thenReturn(mockOut);

        List<DirectorOutDto> result = directorService.findAll(null, null, null);

        assertEquals(1, result.size());
        verify(directorRepository, times(1)).findAll();
    }

    // GET /directors/{id} - 200 OK
    @Test
    public void testFindById() throws DirectorNotFoundException {
        Director mock = createMockDirector(1, "Christopher Nolan", "British");
        DirectorDto mockDto = new DirectorDto();
        mockDto.setId(1L);

        when(directorRepository.findById(1L)).thenReturn(Optional.of(mock));
        when(modelMapper.map(mock, DirectorDto.class)).thenReturn(mockDto);

        DirectorDto result = directorService.findById(1L);

        assertNotNull(result);
        verify(directorRepository, times(1)).findById(1L);
    }

    // POST /directors - 201 CREATED
    @Test
    public void testAdd() {
        Director newDir = createMockDirector(0, "Quentin Tarantino", "American");
        Director saved = createMockDirector(3, "Quentin Tarantino", "American");

        when(directorRepository.save(any(Director.class))).thenReturn(saved);

        Director result = directorService.add(newDir);

        assertNotNull(result);
        verify(directorRepository, times(1)).save(any(Director.class));
    }

    // PUT /directors/{id} - 200 OK
    @Test
    public void testModify() throws DirectorNotFoundException {
        Director existing = createMockDirector(1, "Christopher Nolan", "British");
        Director updated = createMockDirector(1, "Christopher Nolan Updated", "British");

        when(directorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(directorRepository.save(any(Director.class))).thenReturn(updated);

        Director result = directorService.modify(1L, updated);

        assertNotNull(result);
        verify(directorRepository, times(1)).findById(1L);
        verify(directorRepository, times(1)).save(any(Director.class));
    }

    // DELETE /directors/{id} - 204 NO CONTENT
    @Test
    public void testDelete() throws DirectorNotFoundException {
        Director existing = createMockDirector(1, "Christopher Nolan", "British");

        when(directorRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(directorRepository).delete(any(Director.class));

        directorService.delete(1L);

        verify(directorRepository, times(1)).findById(1L);
        verify(directorRepository, times(1)).delete(any(Director.class));
    }

    private Director createMockDirector(long id, String name, String nationality) {
        Director director = new Director();
        director.setId(id);
        director.setName(name);
        director.setNationality(nationality);
        director.setBirthDate(LocalDate.of(1970, 7, 30));
        director.setActive(true);
        director.setAwards(5);
        return director;
    }
}
