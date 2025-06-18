package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerMockTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addUser_shouldReturn200() throws Exception {
        String user = "{\"login\":\"login\",\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"2000-10-20\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser_shouldReturn200() throws Exception {
        String user1 = "{\"login\":\"login\",\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"2000-10-20\"}";
        String user2 = "{\"id\":1,\"login\":\"login2\",\"name\":\"Name2\",\"email\":\"mail2@mail.ru\",\"birthday\":" +
                "\"2000-11-21\"}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user1));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user2))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllUsers_shouldReturn200() throws Exception {
        String user = "{\"login\":\"login\",\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"2000-10-20\"}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("login"))
                .andExpect(jsonPath("$[0].name").value("Name"))
                .andExpect(jsonPath("$[0].email").value("mail@mail.ru"));
    }

    @Test
    public void addUserWithEmptyEmail_shouldReturn400() throws Exception {
        String user = "{\"login\":\"login\",\"name\":\"Name\",\"email\":\"\",\"birthday\":\"2000-10-20\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithBlankEmail_shouldReturn400() throws Exception {
        String user = "{\"login\":\"login\",\"name\":\"Name\",\"email\":\" \",\"birthday\":\"2000-10-20\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithoutEmail_shouldReturn400() throws Exception {
        String user = "{\"login\":\"login\",\"name\":\"Name\",\"birthday\":\"2000-10-20\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithIncorrectEmail_shouldReturn400() throws Exception {
        String user = "{\"login\":\"login\",\"name\":\"Name\",\"email\":\"mail\",\"birthday\":\"2000-10-20\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithEmptyLogin_shouldReturn400() throws Exception {
        String user = "{\"login\":\"\",\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"2000-10-20\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithBlankLogin_shouldReturn400() throws Exception {
        String user = "{\"login\":\" \",\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"2000-10-20\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithLoginWithBlank_shouldReturn400() throws Exception {
        String user = "{\"login\":\"us er\",\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"2000-10-20\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithoutLogin_shouldReturn400() throws Exception {
        String user = "{\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"2000-10-20\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithIncorrectBirthday_shouldReturn400() throws Exception {
        String user = "{\"login\":\"user\",\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"2030-10-20\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserIncorrectEmail_shouldReturn400() throws Exception {
        String user1 = "{\"login\":\"login\",\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"2000-10-20\"}";
        String user2 = "{\"id\":1,\"login\":\"\",\"name\":\"Name2\",\"email\":\"mail2\",\"birthday\":\"2000-11-21\"}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user1));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user2))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserWithEmptyLogin_shouldReturn400() throws Exception {
        String user1 = "{\"login\":\"login\",\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"2000-10-20\"}";
        String user2 = "{\"id\":1,\"login\":\"\",\"name\":\"Name2\",\"email\":\"mail2@mail.ru\",\"birthday\":" +
                "\"2000-11-21\"}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user1));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user2))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserWithLoginWithBlank_shouldReturn400() throws Exception {
        String user1 = "{\"login\":\"login\",\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"2000-10-20\"}";
        String user2 = "{\"id\":1,\"login\":\"us er\",\"name\":\"Name2\",\"email\":\"mail2@mail.ru\",\"birthday\":" +
                "\"2000-11-21\"}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user1));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user2))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserWithIncorrectBirthday_shouldReturn400() throws Exception {
        String user1 = "{\"login\":\"login\",\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":\"2000-10-20\"}";
        String user2 = "{\"id\":1,\"login\":\"user\",\"name\":\"Name2\",\"email\":\"mail2@mail.ru\",\"birthday\":" +
                "\"2030-11-21\"}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user1));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user2))
                .andExpect(status().isBadRequest());
    }
}