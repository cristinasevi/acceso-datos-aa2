package acceso.datos.aa2.movies;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import acceso.datos.aa2.movies.controller.UserController;
import acceso.datos.aa2.movies.domain.User;
import acceso.datos.aa2.movies.dto.UserDto;
import acceso.datos.aa2.movies.dto.UserOutDto;
import acceso.datos.aa2.movies.exception.UserNotFoundException;
import acceso.datos.aa2.movies.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // GET /users - 200 OK
    @Test
    public void testGetAll200() throws Exception {
        List<UserOutDto> usersOutDto = List.of(
                new UserOutDto(1L, "csevi", "Cristina", "Serrano", "csevi@gmail.com", true),
                new UserOutDto(2L, "mdiaz", "Marta", "Díaz", "mdiaz@gmail.com", false)
        );

        when(userService.findAll(null, null, null)).thenReturn(usersOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<UserOutDto> usersListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(usersListResponse);
        assertEquals(2, usersListResponse.size());
    }

    // GET /users/{id} - 200 OK
    @Test
    public void testGetById200() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("csevi");

        when(userService.findById(1L)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    // GET /users/{id} - 404 NOT FOUND
    @Test
    public void testGetById404() throws Exception {
        when(userService.findById(999L)).thenThrow(new UserNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/999")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    // POST /users - 201 CREATED
    @Test
    public void testAddUser201() throws Exception {
        User newUser = new User();
        newUser.setUsername("jlopez");
        newUser.setName("Juan");
        newUser.setSurname("López");
        newUser.setEmail("jlopez@gmail.com");
        newUser.setPassword("password123");
        newUser.setBirthDate(LocalDate.of(1995, 3, 15));

        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setUsername("jlopez");

        when(userService.add(any(User.class))).thenReturn(savedUser);

        String userJson = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());
    }

    // POST /users - 400 BAD REQUEST
    @Test
    public void testAddUser400() throws Exception {
        String invalidUserJson = "{ \"name\": \"Juan\" }"; // Falta "username"

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());
    }

    // PUT /users/{id} - 200 OK
    @Test
    public void testModifyUser200() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("csevi");

        when(userService.modify(anyLong(), any(User.class))).thenReturn(updatedUser);

        String userJson = objectMapper.writeValueAsString(updatedUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());
    }

    // PUT /users/{id} - 404 NOT FOUND
    @Test
    public void testModifyUser404() throws Exception {
        User updatedUser = new User();
        updatedUser.setUsername("nonexistent");

        when(userService.modify(anyLong(), any(User.class))).thenThrow(new UserNotFoundException());

        String userJson = objectMapper.writeValueAsString(updatedUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isNotFound());
    }

    // DELETE /users/{id} - 204 NO CONTENT
    @Test
    public void testDeleteUser204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE /users/{id} - 404 NOT FOUND
    @Test
    public void testDeleteUser404() throws Exception {
        org.mockito.Mockito.doThrow(new UserNotFoundException()).when(userService).delete(999L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/999"))
                .andExpect(status().isNotFound());
    }
}
