package br.edu.ifpb.instagram.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.model.request.LoginRequest;
import br.edu.ifpb.instagram.service.UserService;
import br.edu.ifpb.instagram.service.impl.UserServiceImpl;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthControllerTest {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldReturnBadRequest_whenMissingUserArguments() throws Exception {
        UserDto userDTO = new UserDto(null, "userTest5", null, "user5@test.com", "null", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        UserDto userDTO = new UserDto(null, "userTest1", "user1", "user1@test.com", "passwordTest", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(Matchers.greaterThan(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("userTest1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("user1@test.com"));
    }

    @Test
    void shouldAuthenticateUserSuccessfully() throws Exception {

        UserDto userDTO = new UserDto(null, "userTest2", "user2", "user2@test.com", "passwordTest", null);
        userService.createUser(userDTO);

        LoginRequest credencials = new LoginRequest("user2", "passwordTest");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credencials)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("user2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());

    }

    @Test
    void shouldReturnForbiddenRequest_whenAuthenticatingWithWrongPassword() throws Exception {

        UserDto userDTO = new UserDto(null, "userTest3", "user3", "user3@test.com", "passwordTest", null);
        userService.createUser(userDTO);

        LoginRequest credencials = new LoginRequest("user3", "wrongPasswordTest");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credencials)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

    }

    @Test
    void shouldReturnBadRequest_whenNotUsingCredentialsToAuthenticate() throws Exception {

        UserDto userDTO = new UserDto(null, "userTest4", "user4", "user4@test.com", "passwordTest", null);
        userService.createUser(userDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

}
