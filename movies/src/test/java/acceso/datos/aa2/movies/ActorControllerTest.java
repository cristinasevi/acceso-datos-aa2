package acceso.datos.aa2.movies;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import acceso.datos.aa2.movies.controller.ActorController;
import acceso.datos.aa2.movies.domain.Actor;
import acceso.datos.aa2.movies.dto.ActorDto;
import acceso.datos.aa2.movies.dto.ActorOutDto;
import acceso.datos.aa2.movies.exception.ActorNotFoundException;
import acceso.datos.aa2.movies.service.ActorService;
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

@WebMvcTest(ActorController.class)
public class ActorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActorService actorService;

    @Autowired
    private ObjectMapper objectMapper;

    // GET /actors - 200 OK
    @Test
    public void testGetAll200() throws Exception {
        List<ActorOutDto> actorsOutDto = List.of(
                new ActorOutDto(1L, "Leonardo DiCaprio", "American", true, "Lead"),
                new ActorOutDto(2L, "Tom Hanks", "American", true, "Lead")
        );

        when(actorService.findAll(null, null, null)).thenReturn(actorsOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/actors")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<ActorOutDto> actorsListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(actorsListResponse);
        assertEquals(2, actorsListResponse.size());
    }

    // GET /actors/{id} - 200 OK
    @Test
    public void testGetById200() throws Exception {
        ActorDto actorDto = new ActorDto(1L, "Leonardo DiCaprio", "American",
                LocalDate.of(1974, 11, 11), true, 1, "Award-winning actor", "Lead", 50);

        when(actorService.findById(1L)).thenReturn(actorDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/actors/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    // GET /actors/{id} - 404 NOT FOUND
    @Test
    public void testGetById404() throws Exception {
        when(actorService.findById(999L)).thenThrow(new ActorNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/actors/999")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    // POST /actors - 201 CREATED
    @Test
    public void testAddActor201() throws Exception {
        Actor newActor = new Actor();
        newActor.setName("Brad Pitt");
        newActor.setNationality("American");
        newActor.setBirthDate(LocalDate.of(1963, 12, 18));
        newActor.setActive(true);
        newActor.setAwards(2);

        Actor savedActor = new Actor();
        savedActor.setId(3L);
        savedActor.setName("Brad Pitt");

        when(actorService.add(any(Actor.class))).thenReturn(savedActor);

        String actorJson = objectMapper.writeValueAsString(newActor);

        mockMvc.perform(MockMvcRequestBuilders.post("/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(actorJson))
                .andExpect(status().isCreated());
    }

    // POST /actors - 400 BAD REQUEST
    @Test
    public void testAddActor400() throws Exception {
        String invalidActorJson = "{ \"nationality\": \"American\" }"; // Falta "name" obligatorio

        mockMvc.perform(MockMvcRequestBuilders.post("/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidActorJson))
                .andExpect(status().isBadRequest());
    }

    // PUT /actors/{id} - 200 OK
    @Test
    public void testModifyActor200() throws Exception {
        Actor updatedActor = new Actor();
        updatedActor.setId(1L);
        updatedActor.setName("Leonardo DiCaprio");

        when(actorService.modify(anyLong(), any(Actor.class))).thenReturn(updatedActor);

        String actorJson = objectMapper.writeValueAsString(updatedActor);

        mockMvc.perform(MockMvcRequestBuilders.put("/actors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(actorJson))
                .andExpect(status().isOk());
    }

    // PUT /actors/{id} - 404 NOT FOUND
    @Test
    public void testModifyActor404() throws Exception {
        Actor updatedActor = new Actor();
        updatedActor.setName("NonExistent Actor");

        when(actorService.modify(anyLong(), any(Actor.class))).thenThrow(new ActorNotFoundException());

        String actorJson = objectMapper.writeValueAsString(updatedActor);

        mockMvc.perform(MockMvcRequestBuilders.put("/actors/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(actorJson))
                .andExpect(status().isNotFound());
    }

    // DELETE /actors/{id} - 204 NO CONTENT
    @Test
    public void testDeleteActor204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/actors/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE /actors/{id} - 404 NOT FOUND
    @Test
    public void testDeleteActor404() throws Exception {
        org.mockito.Mockito.doThrow(new ActorNotFoundException()).when(actorService).delete(999L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/actors/999"))
                .andExpect(status().isNotFound());
    }
}
