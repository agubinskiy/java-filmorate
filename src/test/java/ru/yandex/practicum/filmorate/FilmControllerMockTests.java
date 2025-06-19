package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmControllerMockTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @Test
    public void addFilm_shouldReturn200() throws Exception {
        String filmJson = "{\"name\":\"Name\",\"description\":\"Description\",\"releaseDate\":\"2000-10-20\"," +
                "\"duration\":120}";

        Film expectedFilm = new Film(1L, "Name", "Description",
                LocalDate.of(2000, 10, 20), 120);
        when(filmService.addFilm(any(Film.class))).thenReturn(expectedFilm);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.releaseDate").value("2000-10-20"))
                .andExpect(jsonPath("$.duration").value(120));
    }

    @Test
    public void updateFilm_shouldReturn200() throws Exception {
        String film = "{\"id\":1,\"name\":\"Name2\",\"description\":\"Description2\",\"releaseDate\":\"2000-11-21\"," +
                "\"duration\":130}";

        Film expectedFilm = new Film(1L, "Name2", "Description2",
                LocalDate.of(2000, 11, 21), 130);
        when(filmService.updateFilm(any(Film.class))).thenReturn(expectedFilm);


        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Name2"))
                .andExpect(jsonPath("$.description").value("Description2"))
                .andExpect(jsonPath("$.releaseDate").value("2000-11-21"))
                .andExpect(jsonPath("$.duration").value(130));
    }

    @Test
    public void getAllFilms_shouldReturn200() throws Exception {
        Film film1 = new Film(1L, "Name", "Description", LocalDate.parse("2000-10-20"),
                120);
        Film film2 = new Film(2L, "Name2", "Description2", LocalDate.parse("2000-11-21"),
                130);

        when(filmService.findAllFilms()).thenReturn(List.of(film1, film2));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Name"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].releaseDate").value("2000-10-20"))
                .andExpect(jsonPath("$[0].duration").value(120))
                .andExpect(jsonPath("$[1].name").value("Name2"))
                .andExpect(jsonPath("$[1].description").value("Description2"))
                .andExpect(jsonPath("$[1].releaseDate").value("2000-11-21"))
                .andExpect(jsonPath("$[1].duration").value(130));
    }

    @Test
    public void
    addFilmWithEmptyName_shouldReturn400() throws Exception {
        String film = "{\"name\":\"\",\"description\":\"Description\",\"releaseDate\":\"2000-10-20\"," +
                "\"duration\":120}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithBlankName_shouldReturn400() throws Exception {
        String film = "{\"name\":\" \",\"description\":\"Description\",\"releaseDate\":\"2000-10-20\"," +
                "\"duration\":120}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithoutName_shouldReturn400() throws Exception {
        String film = "{\"description\":\"Description\",\"releaseDate\":\"2000-10-20\",\"duration\":120}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWith200symbolsDescription_shouldReturn200() throws Exception {
        String film = "{\"name\":\"Name\",\"description\":\"Тестовое описание фильма, проверка передачи в поле с " +
                "описанием более 200 символов. ТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТест" +
                "ТестТестТестТестТестТестТестТестТестТестТестТест1\",\"releaseDate\":\"2000-10-20\",\"duration\":120}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isOk());
    }

    @Test
    public void addFilmWithTooLongDescription_shouldReturn400() throws Exception {
        String film = "{\"name\":\"Name\",\"description\":\"Тестовое описание фильма, проверка передачи в поле " +
                "с описанием более 200 символов. ТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТест" +
                "ТестТестТестТестТестТестТестТестТестТестТестТестТест11\",\"releaseDate\":\"2000-10-20\"," +
                "\"duration\":120}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithDuration1_shouldReturn200() throws Exception {
        String film = "{\"name\":\"Name\",\"description\":\"Description\",\"releaseDate\":\"2000-10-20\"," +
                "\"duration\":1}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isOk());
    }

    @Test
    public void addFilmWithDuration0_shouldReturn400() throws Exception {
        String film = "{\"name\":\"Name\",\"description\":\"Description\",\"releaseDate\":\"2000-10-20\"," +
                "\"duration\":0}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithDurationLessThan0_shouldReturn400() throws Exception {
        String film = "{\"name\":\"Name\",\"description\":\"Description\",\"releaseDate\":\"2000-10-20\"," +
                "\"duration\":-1}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFilmWithEmptyName_shouldReturn400() throws Exception {
        String film = "{\"id\":1,\"name\":\"\",\"description\":\"Description2\",\"releaseDate\":" +
                "\"2000-11-21\",\"duration\":130}";

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFilmWithoutName_shouldReturn400() throws Exception {
        String film = "{\"id\":1,\"description\":\"Description2\",\"releaseDate\":\"2000-11-21\",\"duration\":130}";

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFilmWithTooLongDescription_shouldReturn400() throws Exception {
        String film = "{\"id\":1,\"name\":\"Name2\",\"description\":\"Тестовое описание фильма, проверка передачи " +
                "в поле с описанием более 200 символов. ТестТестТестТестТестТестТестТестТестТестТестТестТестТестТест" +
                "ТестТестТестТестТестТестТестТестТестТестТестТестТестТест11\",\"releaseDate\":\"2000-11-21\"," +
                "\"duration\":130}";

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFilmWithDuration0_shouldReturn400() throws Exception {
        String film = "{\"id\":1,\"name\":\"Name2\",\"description\":\"Description2\",\"releaseDate\":\"2000-11-21\"," +
                "\"duration\":0}";

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getFilm_shouldReturn200() throws Exception {
        Film expectedFilm = new Film(1L, "Name", "Description",
                LocalDate.of(2000, 10, 20), 120);
        when(filmService.getFilm(1L)).thenReturn(expectedFilm);

        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.releaseDate").value("2000-10-20"))
                .andExpect(jsonPath("$.duration").value(120));
    }


}