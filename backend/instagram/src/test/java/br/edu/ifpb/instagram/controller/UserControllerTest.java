package br.edu.ifpb.instagram.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.model.request.UserDetailsRequest;
import br.edu.ifpb.instagram.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    List<UserEntity> userEntities = new ArrayList<>();

    @BeforeEach
    void setUp() {
        UserEntity user1 = new UserEntity();
        user1.setFullName("João Silva");
        user1.setUsername("joaosilva");
        user1.setEncryptedPassword("senha123");
        user1.setEmail("joao.silva@example.com");
        UserEntity user2 = new UserEntity();
        user2.setFullName("Maria Oliveira");
        user2.setUsername("mariaoliveira");
        user2.setEncryptedPassword("senha456");
        user2.setEmail("maria.oliveira@example.com");

        UserEntity user3 = new UserEntity();
        user3.setFullName("Carlos Souza");
        user3.setUsername("carlossouza");
        user3.setEncryptedPassword("senha789");
        user3.setEmail("carlos.souza@example.com");

        userRepository.saveAll(Arrays.asList(user1, user2, user3));
        userEntities.add(user1);
        userEntities.add(user2);
        userEntities.add(user3);
    }

    @AfterEach
    void tearDown() {
        userEntities.removeAll(userEntities);
        userRepository.deleteAll();
    }

    @Test
    void testDeleteUser() throws Exception {
        var id = userEntities.get(1).getId();
        mockMvc.perform(delete("/users/{id}", id))
               .andExpect(status().isOk())
               .andExpect(content().string("user was deleted!"));
    }

    @Test
    void testUpdateUser() throws Exception{
        var user = userEntities.get(1);
         UserDetailsRequest updateRequest = new UserDetailsRequest(user.getId(), user.getEmail(),
          user.getEncryptedPassword(), "Novo Nome", user.getUsername());

        mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
               .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(user.getId()))
               .andExpect(jsonPath("$.fullName").value(updateRequest.fullName()));
    }

    @Test
    void getUsers_shouldReturnUserList() throws Exception {
        var indexUser = 0;
        var idUser = userEntities.get(indexUser).getId();

        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(userEntities.size()))
                .andExpect(jsonPath("$[" + indexUser + "].id").value(idUser))
                .andExpect(jsonPath("$[" + indexUser + "].fullName").value("João Silva"))
                .andExpect(jsonPath("$[" + indexUser + "].username").value("joaosilva"))
                .andExpect(jsonPath("$[" + indexUser + "].email").value("joao.silva@example.com"));
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {

        var idUser = userEntities.get(1).getId();

        mockMvc.perform(get("/users/" + idUser).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idUser))
                .andExpect(jsonPath("$.fullName").value("Maria Oliveira"))
                .andExpect(jsonPath("$.username").value("mariaoliveira"))
                .andExpect(jsonPath("$.email").value("maria.oliveira@example.com"));
    }

}
