package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerMockTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void addUser_shouldReturn200() throws Exception {
        String userJson = "{\"login\":\"login\",\"name\":\"Name\",\"email\":\"mail@mail.ru\",\"birthday\":" +
                "\"2000-10-20\"}";

        User expectedUser = new User(1L, "mail@mail.ru", "login", "Name",
                LocalDate.of(2000, 10, 20), Collections.emptySet());
        when(userService.addUser(any(NewUserRequest.class))).thenReturn(UserMapper.mapToUserDto(expectedUser));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(jsonPath("$.birthday").value("2000-10-20"));
    }

    @Test
    public void updateUser_shouldReturn200() throws Exception {
        String userJson = "{\"id\":1,\"login\":\"login2\",\"name\":\"Name2\",\"email\":\"mail2@mail.ru\",\"birthday\":" +
                "\"2000-11-21\"}";

        User expectedUser = new User(1L, "mail2@mail.ru", "login2", "Name2",
                LocalDate.of(2000, 11, 21), Collections.emptySet());
        when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn(UserMapper.mapToUserDto(expectedUser));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("login2"))
                .andExpect(jsonPath("$.name").value("Name2"))
                .andExpect(jsonPath("$.email").value("mail2@mail.ru"))
                .andExpect(jsonPath("$.birthday").value("2000-11-21"));
    }

    @Test
    public void getAllUsers_shouldReturn200() throws Exception {
        User user1 = new User(1L, "mail@mail.ru", "login", "Name",
                LocalDate.of(2000, 10, 20), Collections.emptySet());
        User user2 = new User(1L, "mail2@mail.ru", "login2", "Name2",
                LocalDate.of(2000, 11, 21), Collections.emptySet());

        when(userService.findAllUsers()).thenReturn(List.of(UserMapper.mapToUserDto(user1),
                UserMapper.mapToUserDto(user2)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].login").value("login"))
                .andExpect(jsonPath("$[0].name").value("Name"))
                .andExpect(jsonPath("$[0].email").value("mail@mail.ru"))
                .andExpect(jsonPath("$[0].birthday").value("2000-10-20"))
                .andExpect(jsonPath("$[1].login").value("login2"))
                .andExpect(jsonPath("$[1].name").value("Name2"))
                .andExpect(jsonPath("$[1].email").value("mail2@mail.ru"))
                .andExpect(jsonPath("$[1].birthday").value("2000-11-21"));
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
        String user = "{\"id\":1,\"login\":\"\",\"name\":\"Name2\",\"email\":\"mail2\",\"birthday\":\"2000-11-21\"}";


        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserWithEmptyLogin_shouldReturn400() throws Exception {
        String user = "{\"id\":1,\"login\":\"\",\"name\":\"Name2\",\"email\":\"mail2@mail.ru\",\"birthday\":" +
                "\"2000-11-21\"}";

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserWithLoginWithBlank_shouldReturn400() throws Exception {
        String user = "{\"id\":1,\"login\":\"us er\",\"name\":\"Name2\",\"email\":\"mail2@mail.ru\",\"birthday\":" +
                "\"2000-11-21\"}";

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserWithIncorrectBirthday_shouldReturn400() throws Exception {
        String user = "{\"id\":1,\"login\":\"user\",\"name\":\"Name2\",\"email\":\"mail2@mail.ru\",\"birthday\":" +
                "\"2030-11-21\"}";

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest());
    }
}