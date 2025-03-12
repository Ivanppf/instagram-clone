package br.edu.ifpb.instagram.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.print.attribute.standard.Media;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
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

    // @Test
    // void testDeleteUser() throws Exception {
    // var id = userEntities.get(1).getId();
    // mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/{id}", id)
    // .contentType(MediaType.APPLICATION_JSON));
    // }

    // @Test
    // void testUpdateUser() {

    // }

    @Test
    void getUsers_shouldReturnForbiddenStatus() throws Exception {
        mockMvc.perform(get("/api/v1/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

}
