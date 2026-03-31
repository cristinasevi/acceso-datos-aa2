package acceso.datos.aa2.movies;

import acceso.datos.aa2.movies.domain.Studio;
import acceso.datos.aa2.movies.dto.StudioDto;
import acceso.datos.aa2.movies.dto.StudioOutDto;
import acceso.datos.aa2.movies.exception.StudioNotFoundException;
import acceso.datos.aa2.movies.repository.StudioRepository;
import acceso.datos.aa2.movies.service.StudioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudioServiceTest {

    @InjectMocks
    private StudioService studioService;

    @Mock
    private StudioRepository studioRepository;

    @Mock
    private ModelMapper modelMapper;

    // GET /studios - 200 OK
    @Test
    public void testFindAll() {
        List<Studio> mockList = List.of(createMockStudio(1, "Warner Bros", "USA"));
        List<StudioOutDto> mockOut = List.of(new StudioOutDto(1L, "Warner Bros", "USA", true, 34.1522, -118.3437));

        when(studioRepository.findAll()).thenReturn(mockList);
        when(modelMapper.map(mockList, new TypeToken<List<StudioOutDto>>() {}.getType())).thenReturn(mockOut);

        List<StudioOutDto> result = studioService.findAll(null, null, null);

        assertEquals(1, result.size());
        verify(studioRepository, times(1)).findAll();
    }

    // GET /studios/{id} - 200 OK
    @Test
    public void testFindById() throws StudioNotFoundException {
        Studio mock = createMockStudio(1, "Warner Bros", "USA");
        StudioDto mockDto = new StudioDto();
        mockDto.setId(1L);

        when(studioRepository.findById(1L)).thenReturn(Optional.of(mock));
        when(modelMapper.map(mock, StudioDto.class)).thenReturn(mockDto);

        StudioDto result = studioService.findById(1L);

        assertNotNull(result);
        verify(studioRepository, times(1)).findById(1L);
    }

    // POST /studios - 201 CREATED
    @Test
    public void testAdd() {
        Studio newStudio = createMockStudio(0, "Paramount Pictures", "USA");
        Studio saved = createMockStudio(3, "Paramount Pictures", "USA");

        when(studioRepository.save(any(Studio.class))).thenReturn(saved);

        Studio result = studioService.add(newStudio);

        assertNotNull(result);
        verify(studioRepository, times(1)).save(any(Studio.class));
    }

    // PUT /studios/{id} - 200 OK
    @Test
    public void testModify() throws StudioNotFoundException {
        Studio existing = createMockStudio(1, "Warner Bros", "USA");
        Studio updated = createMockStudio(1, "Warner Bros Updated", "USA");

        when(studioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studioRepository.save(any(Studio.class))).thenReturn(updated);

        Studio result = studioService.modify(1L, updated);

        assertNotNull(result);
        verify(studioRepository, times(1)).findById(1L);
        verify(studioRepository, times(1)).save(any(Studio.class));
    }

    // DELETE /studios/{id} - 204 NO CONTENT
    @Test
    public void testDelete() throws StudioNotFoundException {
        Studio existing = createMockStudio(1, "Warner Bros", "USA");

        when(studioRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(studioRepository).delete(any(Studio.class));

        studioService.delete(1L);

        verify(studioRepository, times(1)).findById(1L);
        verify(studioRepository, times(1)).delete(any(Studio.class));
    }

    private Studio createMockStudio(long id, String name, String country) {
        Studio studio = new Studio();
        studio.setId(id);
        studio.setName(name);
        studio.setCountry(country);
        studio.setFoundationYear(1923);
        studio.setActive(true);
        studio.setLatitude(34.1522);
        studio.setLongitude(-118.3437);
        return studio;
    }
}
