package com.example.UserApi;

import com.example.UserApi.controller.UserController;
import com.example.UserApi.dto.UserRequestDto;
import com.example.UserApi.exceptions.InvalidInputException;
import com.example.UserApi.model.User;
import com.example.UserApi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
public class UserApiControllerTests {
    private static final Long ID= 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final User validUser = new User(
            1L,
            "email@mail.com",
            "Name",
            "LastName",
            LocalDate.of(1990, 6, 1),
            null,
            null);

    private final UserRequestDto validDto = new UserRequestDto(
            "email@mail.com",
            null,
            null,
            LocalDate.of(1990, 1, 1),
            null,
            null
    );

    private final User invalidUser = new User();
    private final UserRequestDto invalidDto = new UserRequestDto(
            "emailmailcom",
            null,
            null,
            LocalDate.of(3990, 1, 1),
            null,
            null
    );

    private final List<User> validUsers = Arrays.asList(validUser, validUser);
    private final List<User> invalidUsers = Arrays.asList(invalidUser, invalidUser);


    @Test
    void getUsersByBirthDateRange_WithValidRange_Ok() throws Exception {
        LocalDate from = LocalDate.of(1990, 1, 1);
        LocalDate to = LocalDate.of(2000, 1, 1);

        when(userService.findUsersByBirthDateRange(eq(from), eq(to))).thenReturn(validUsers);

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(validUsers.size()));
    }

    @Test
    void createUser_WithValidData_Ok() throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn(validUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateUser_WithValidData_Ok() throws Exception {
        when(userService.updateUser(eq(ID), any(UserRequestDto.class))).thenReturn(validUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void patchUser_ValidData_Ok() throws Exception {
        when(userService.patchUser(eq(ID), any(UserRequestDto.class))).thenReturn(validUser);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void createUser_WithInvalidData_ValidationFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateUser_InvalidData_ValidationFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void patchUser_InvalidData_ValidationFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getUsersByBirthDateRange_InvalidData_ValidationFailed() throws Exception {
        LocalDate from = LocalDate.of(2000, 1, 1);
        LocalDate to = LocalDate.of(1990, 1, 1);

        when(userService.findUsersByBirthDateRange(any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new InvalidInputException(""));

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
