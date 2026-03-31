package acceso.datos.aa2.movies;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import acceso.datos.aa2.movies.controller.DirectorController;
import acceso.datos.aa2.movies.domain.Director;
import acceso.datos.aa2.movies.dto.DirectorDto;
import acceso.datos.aa2.movies.dto.DirectorOutDto;
import acceso.datos.aa2.movies.exception.DirectorNotFoundException;
import acceso.datos.aa2.movies.service.DirectorService;
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

@WebMvcTest(DirectorController.class)
public class DirectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DirectorService directorService;

    @Autowired
    private ObjectMapper objectMapper;

    // GET /directors - 200 OK
    @Test
    public void testGetAll200() throws Exception {
        List<DirectorOutDto> directorsOutDto = List.of(
                new DirectorOutDto(1L, "Christopher Nolan", "British", true),
                new DirectorOutDto(2L, "Steven Spielberg", "American", true)
        );

        when(directorService.findAll(null, null, null)).thenReturn(directorsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/directors")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<DirectorOutDto> directorsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(directorsListResponse);
        assertEquals(2, directorsListResponse.size());
    }

    // GET /directors/{id} - 200 OK
    @Test
    public void testGetById200() throws Exception {
        DirectorDto directorDto = new DirectorDto(1L, "Christopher Nolan", "British",
                LocalDate.of(1970, 7, 30), true, 24, "British film director", 55);

        when(directorService.findById(1L)).thenReturn(directorDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/directors/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    // GET /directors/{id} - 404 NOT FOUND
    @Test
    public void testGetById404() throws Exception {
        when(directorService.findById(999L)).thenThrow(new DirectorNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/directors/999")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    // POST /directors - 201 CREATED
    @Test
    public void testAddDirector201() throws Exception {
        Director newDirector = new Director();
        newDirector.setName("Quentin Tarantino");
        newDirector.setNationality("American");
        newDirector.setBirthDate(LocalDate.of(1963, 3, 27));

        Director savedDirector = new Director();
        savedDirector.setId(3L);
        savedDirector.setName("Quentin Tarantino");

        when(directorService.add(any(Director.class))).thenReturn(savedDirector);

        String directorJson = objectMapper.writeValueAsString(newDirector);

        mockMvc.perform(MockMvcRequestBuilders.post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(directorJson))
                .andExpect(status().isCreated());
    }

    // POST /directors - 400 BAD REQUEST
    @Test
    public void testAddDirector400() throws Exception {
        String invalidDirectorJson = "{ \"nationality\": \"American\" }"; // Falta "name"

        mockMvc.perform(MockMvcRequestBuilders.post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDirectorJson))
                .andExpect(status().isBadRequest());
    }

    // PUT /directors/{id} - 200 OK
    @Test
    public void testModifyDirector200() throws Exception {
        Director updatedDirector = new Director();
        updatedDirector.setId(1L);
        updatedDirector.setName("Christopher Nolan");

        when(directorService.modify(anyLong(), any(Director.class))).thenReturn(updatedDirector);

        String directorJson = objectMapper.writeValueAsString(updatedDirector);

        mockMvc.perform(MockMvcRequestBuilders.put("/directors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(directorJson))
                .andExpect(status().isOk());
    }

    // PUT /directors/{id} - 404 NOT FOUND
    @Test
    public void testModifyDirector404() throws Exception {
        Director updatedDirector = new Director();
        updatedDirector.setName("NonExistent Director");

        when(directorService.modify(anyLong(), any(Director.class))).thenThrow(new DirectorNotFoundException());

        String directorJson = objectMapper.writeValueAsString(updatedDirector);

        mockMvc.perform(MockMvcRequestBuilders.put("/directors/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(directorJson))
                .andExpect(status().isNotFound());
    }

    // DELETE /directors/{id} - 204 NO CONTENT
    @Test
    public void testDeleteDirector204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/directors/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE /directors/{id} - 404 NOT FOUND
    @Test
    public void testDeleteDirector404() throws Exception {
        org.mockito.Mockito.doThrow(new DirectorNotFoundException()).when(directorService).delete(999L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/directors/999"))
                .andExpect(status().isNotFound());
    }
}
