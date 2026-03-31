package acceso.datos.aa2.movies;

import acceso.datos.aa2.movies.domain.User;
import acceso.datos.aa2.movies.dto.UserDto;
import acceso.datos.aa2.movies.dto.UserOutDto;
import acceso.datos.aa2.movies.exception.UserNotFoundException;
import acceso.datos.aa2.movies.repository.UserRepository;
import acceso.datos.aa2.movies.service.UserService;
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
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    // GET /users - 200 OK
    @Test
    public void testFindAll() {
        List<User> mockList = List.of(createMockUser(1, "csevi", "Cristina", "Serrano"));
        List<UserOutDto> mockOut = List.of(new UserOutDto(1L, "csevi", "Cristina", "Serrano", "csevi@gmail.com", true));

        when(userRepository.findAll()).thenReturn(mockList);
        when(modelMapper.map(mockList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(mockOut);

        List<UserOutDto> result = userService.findAll(null, null, null);

        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    // GET /users/{id} - 200 OK
    @Test
    public void testFindById() throws UserNotFoundException {
        User mock = createMockUser(1, "csevi", "Cristina", "Serrano");
        UserDto mockDto = new UserDto();
        mockDto.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mock));
        when(modelMapper.map(mock, UserDto.class)).thenReturn(mockDto);

        UserDto result = userService.findById(1L);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
    }

    // POST /users - 201 CREATED
    @Test
    public void testAdd() {
        User newUser = createMockUser(0, "jlopez", "Juan", "López");
        User saved = createMockUser(3, "jlopez", "Juan", "López");

        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.add(newUser);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // PUT /users/{id} - 200 OK
    @Test
    public void testModify() throws UserNotFoundException {
        User existing = createMockUser(1, "csevi", "Cristina", "Serrano");
        User updated = createMockUser(1, "csevi", "Cristina", "Serrano");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        User result = userService.modify(1L, updated);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // DELETE /users/{id} - 204 NO CONTENT
    @Test
    public void testDelete() throws UserNotFoundException {
        User existing = createMockUser(1, "csevi", "Cristina", "Serrano");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        doNothing().when(userRepository).delete(any(User.class));

        userService.delete(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(any(User.class));
    }

    private User createMockUser(long id, String username, String name, String surname) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(username + "@gmail.com");
        user.setPassword("password123");
        user.setRegistrationDate(LocalDate.now());
        user.setPremium(id == 1);
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setActive(true);
        return user;
    }
}
