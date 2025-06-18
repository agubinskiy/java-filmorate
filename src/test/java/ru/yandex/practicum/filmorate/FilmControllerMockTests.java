package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
//можно будет убрать после реализации delete
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmControllerMockTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addFilm_shouldReturn200() throws Exception {
        String film = """
                            {
                              "name": "Name",
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": 120
                            }
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isOk());
    }

    @Test
    public void updateFilm_shouldReturn200() throws Exception {
        String film1 = """
                            {
                              "name": "Name",
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": 120
                            }
                """;
        String film2 = """
                            {
                              "id": 1,
                              "name": "Name2",
                              "description": "Description2",
                              "releaseDate": "2000-11-21",
                              "duration": 130
                            }
                """;

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(film1));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film2))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllFilms_shouldReturn200() throws Exception {
        String film = """
                            {
                              "name": "Name",
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": 120
                            }
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Name"))
                .andExpect(jsonPath("$[0].description").value("Description"));
        ;
    }

    @Test
    public void addFilmWithEmptyName_shouldReturn400() throws Exception {
        String film = """
                            {
                              "name": "",
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": 120
                            }
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithBlankName_shouldReturn400() throws Exception {
        String film = """
                            {
                              "name": " ",
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": 120
                            }
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithoutName_shouldReturn400() throws Exception {
        String film = """
                            {
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": 120
                            }
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWith200symbolsDescription_shouldReturn200() throws Exception {
        String film = """
                            {
                              "name": "Name",
                              "description": "Тестовое описание фильма, проверка передачи в поле с описанием более 200 символов. \
                ТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТест\
                ТестТестТестТест1",
                              "releaseDate": "2000-10-20",
                              "duration": 120
                            }
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isOk());
    }

    @Test
    public void addFilmWithTooLongDescription_shouldReturn400() throws Exception {
        String film = """
                            {
                              "name": "Name",
                              "description": "Тестовое описание фильма, проверка передачи в поле с описанием более 200 символов. \
                ТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТест\
                ТестТестТестТест11",
                              "releaseDate": "2000-10-20",
                              "duration": 120
                            }
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithDuration1_shouldReturn200() throws Exception {
        String film = """
                            {
                              "name": "Name",
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": 1
                            }
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isOk());
    }

    @Test
    public void addFilmWithDuration0_shouldReturn400() throws Exception {
        String film = """
                            {
                              "name": "Name",
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": 0
                            }
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithDurationLessThan0_shouldReturn400() throws Exception {
        String film = """
                            {
                              "name": "Name",
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": -1
                            }
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFilmWithEmptyName_shouldReturn400() throws Exception {
        String film1 = """
                            {
                              "name": "Name",
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": 120
                            }
                """;
        String film2 = """
                            {
                              "id": 1,
                              "name": "",
                              "description": "Description2",
                              "releaseDate": "2000-11-21",
                              "duration": 130
                            }
                """;

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(film1));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film2))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFilmWithoutName_shouldReturn400() throws Exception {
        String film1 = """
                            {
                              "name": "Name",
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": 120
                            }
                """;
        String film2 = """
                            {
                              "id": 1,
                              "description": "Description2",
                              "releaseDate": "2000-11-21",
                              "duration": 130
                            }
                """;

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(film1));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film2))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFilmWithTooLongDescription_shouldReturn400() throws Exception {
        String film1 = """
                            {
                              "name": "Name",
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": 120
                            }
                """;
        String film2 = """
                            {
                              "id": 1,
                              "name": "Name2",
                              "description": "Тестовое описание фильма, проверка передачи в поле с описанием более 200 символов. \
                            ТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТестТест\
                            ТестТестТестТест11",
                              "releaseDate": "2000-11-21",
                              "duration": 130
                            }
                """;

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(film1));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film2))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFilmWithDuration0_shouldReturn400() throws Exception {
        String film1 = """
                            {
                              "name": "Name",
                              "description": "Description",
                              "releaseDate": "2000-10-20",
                              "duration": 120
                            }
                """;
        String film2 = """
                            {
                              "id": 1,
                              "name": "Name2",
                              "description": "Description2",
                              "releaseDate": "2000-11-21",
                              "duration": 0
                            }
                """;

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(film1));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(film2))
                .andExpect(status().isBadRequest());
    }
}
