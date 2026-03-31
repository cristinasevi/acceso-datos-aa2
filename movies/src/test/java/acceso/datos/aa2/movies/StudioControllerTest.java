package acceso.datos.aa2.movies;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import acceso.datos.aa2.movies.controller.StudioController;
import acceso.datos.aa2.movies.domain.Studio;
import acceso.datos.aa2.movies.dto.StudioDto;
import acceso.datos.aa2.movies.dto.StudioOutDto;
import acceso.datos.aa2.movies.exception.StudioNotFoundException;
import acceso.datos.aa2.movies.service.StudioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudioController.class)
public class StudioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudioService studioService;

    @Autowired
    private ObjectMapper objectMapper;

    // GET /studios - 200 OK
    @Test
    public void testGetAll200() throws Exception {
        List<StudioOutDto> studiosOutDto = List.of(
                new StudioOutDto(1L, "Warner Bros", "USA", true, 34.1522, -118.3437),
                new StudioOutDto(2L, "Universal Pictures", "USA", true, 51.5074, -0.1278)
        );

        when(studioService.findAll(null, null, null)).thenReturn(studiosOutDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/studios")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<StudioOutDto> studiosListResponse = objectMapper.readValue(jsonResponse, new TypeReference<>(){});

        assertNotNull(studiosListResponse);
        assertEquals(2, studiosListResponse.size());
    }

    // GET /studios/{id} - 200 OK
    @Test
    public void testGetById200() throws Exception {
        StudioDto studioDto = new StudioDto();
        studioDto.setId(1L);
        studioDto.setName("Warner Bros");

        when(studioService.findById(1L)).thenReturn(studioDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/studios/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    // GET /studios/{id} - 404 NOT FOUND
    @Test
    public void testGetById404() throws Exception {
        when(studioService.findById(999L)).thenThrow(new StudioNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/studios/999")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    // POST /studios - 201 CREATED
    @Test
    public void testAddStudio201() throws Exception {
        Studio newStudio = new Studio();
        newStudio.setName("Paramount Pictures");
        newStudio.setCountry("USA");
        newStudio.setFoundationYear(1912);
        newStudio.setLatitude(34.0522);
        newStudio.setLongitude(-118.2437);

        Studio savedStudio = new Studio();
        savedStudio.setId(3L);
        savedStudio.setName("Paramount Pictures");
        savedStudio.setLatitude(34.0522);
        savedStudio.setLongitude(-118.2437);

        when(studioService.add(any(Studio.class))).thenReturn(savedStudio);

        String studioJson = objectMapper.writeValueAsString(newStudio);

        mockMvc.perform(MockMvcRequestBuilders.post("/studios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studioJson))
                .andExpect(status().isCreated());
    }

    // POST /studios - 400 BAD REQUEST
    @Test
    public void testAddStudio400() throws Exception {
        String invalidStudioJson = "{ \"country\": \"USA\" }"; // Falta "name"

        mockMvc.perform(MockMvcRequestBuilders.post("/studios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidStudioJson))
                .andExpect(status().isBadRequest());
    }

    // PUT /studios/{id} - 200 OK
    @Test
    public void testModifyStudio200() throws Exception {
        Studio updatedStudio = new Studio();
        updatedStudio.setId(1L);
        updatedStudio.setName("Warner Bros");
        updatedStudio.setLatitude(34.1522);
        updatedStudio.setLongitude(-118.3437);

        when(studioService.modify(anyLong(), any(Studio.class))).thenReturn(updatedStudio);

        String studioJson = objectMapper.writeValueAsString(updatedStudio);

        mockMvc.perform(MockMvcRequestBuilders.put("/studios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studioJson))
                .andExpect(status().isOk());
    }

    // PUT /studios/{id} - 404 NOT FOUND
    @Test
    public void testModifyStudio404() throws Exception {
        Studio updatedStudio = new Studio();
        updatedStudio.setName("NonExistent Studio");
        updatedStudio.setLatitude(34.0522);
        updatedStudio.setLongitude(-118.2437);

        when(studioService.modify(anyLong(), any(Studio.class))).thenThrow(new StudioNotFoundException());

        String studioJson = objectMapper.writeValueAsString(updatedStudio);

        mockMvc.perform(MockMvcRequestBuilders.put("/studios/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studioJson))
                .andExpect(status().isNotFound());
    }

    // DELETE /studios/{id} - 204 NO CONTENT
    @Test
    public void testDeleteStudio204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/studios/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE /studios/{id} - 404 NOT FOUND
    @Test
    public void testDeleteStudio404() throws Exception {
        org.mockito.Mockito.doThrow(new StudioNotFoundException()).when(studioService).delete(999L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/studios/999"))
                .andExpect(status().isNotFound());
    }
}
