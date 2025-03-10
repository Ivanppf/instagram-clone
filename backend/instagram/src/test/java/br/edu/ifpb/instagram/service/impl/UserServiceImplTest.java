package br.edu.ifpb.instagram.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.repository.UserRepository;

@SpringBootTest
public class UserServiceImplTest {

    @MockitoBean
    UserRepository userRepository; // Repositório simulado

    @Autowired
    UserServiceImpl userService; // Classe sob teste

    static List<UserEntity> usersEntities;

    @Test
    void testFindById_ReturnsUserDto() {
        // Configurar o comportamento do mock
        Long userId = 1L;

        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(userId);
        mockUserEntity.setFullName("Paulo Pereira");
        mockUserEntity.setEmail("paulo@ppereira.dev");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));

        // Executar o método a ser testado
        UserDto userDto = userService.findById(userId);

        // Verificar o resultado
        assertNotNull(userDto);
        assertEquals(mockUserEntity.getId(), userDto.id());
        assertEquals(mockUserEntity.getFullName(), userDto.fullName());
        assertEquals(mockUserEntity.getEmail(), userDto.email());

        // Verificar a interação com o mock
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindById_ThrowsExceptionWhenUserNotFound() {
        // Configurar o comportamento do mock
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Executar e verificar a exceção
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(userId);
        });

        assertEquals("User not found", exception.getMessage());

        // Verificar a interação com o mock
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testCreate() {
        var userDto = new UserDto(1L, "test_fullname", "username_test", "email_test", "password_test",
                "encoded_password_test");

        when(userRepository.save(any(UserEntity.class))).thenAnswer(invoker -> invoker.getArgument(0));

        var createdUser = userService.createUser(userDto);

        assertNotNull(createdUser);
        assertNotNull(createdUser.id());

        assertEquals(userDto.fullName(), createdUser.fullName());
        assertEquals(userDto.email(), createdUser.email());
        assertEquals(userDto.username(), createdUser.username());

        assertNull(createdUser.password());
        assertNull(createdUser.encryptedPassword());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testUpdate_ReturnsUserDto() {

        var userDto = new UserDto(1L, "test_fullname", "username_test", "email_test", "password_test",
                "encoded_password_test");

        when(userRepository.updatePartialUser(anyString(), anyString(), anyString(),
                anyString(), anyLong())).thenReturn(Integer.valueOf(1));

        var updatedUser = userService.updateUser(userDto);

        assertNotNull(updatedUser);

        assertEquals("username_test", updatedUser.username());

        verify(userRepository).updatePartialUser(updatedUser.fullName(), updatedUser.email(), updatedUser.username(),
                updatedUser.encryptedPassword(), updatedUser.id());
    }

    @Test
    void testUpdate_ThrowsExceptionWhenUserNotFound() {

        var userDto = new UserDto(1L, "test_fullname", "username_test", "email_test", null,
                null);

        when(userRepository.updatePartialUser(anyString(), anyString(), anyString(),
                anyString(), anyLong())).thenReturn(Integer.valueOf(0));

        assertThrows(RuntimeException.class, () -> userService.updateUser(userDto));

        verify(userRepository).updatePartialUser(userDto.fullName(), userDto.email(), userDto.username(),
                userDto.encryptedPassword(), userDto.id());
    }

    @BeforeAll
    static void setUp() {
        usersEntities = new ArrayList<>();
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setFullName("João Silva");
        user1.setUsername("joaosilva");
        user1.setEncryptedPassword("senha123");
        user1.setEmail("joao.silva@example.com");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setFullName("Maria Oliveira");
        user2.setUsername("mariaoliveira");
        user2.setEncryptedPassword("senha456");
        user2.setEmail("maria.oliveira@example.com");

        UserEntity user3 = new UserEntity();
        user3.setId(3L);
        user3.setFullName("Carlos Souza");
        user3.setUsername("carlossouza");
        user3.setEncryptedPassword("senha789");
        user3.setEmail("carlos.souza@example.com");

        usersEntities.add(user1);
        usersEntities.add(user2);
        usersEntities.add(user3);
    }

    @Test
    void testFindAll_ReturnsListUserDto() {
        when(userRepository.findAll()).thenReturn(usersEntities);
        List<UserDto> usersDto = userService.findAll();
        assertEquals(usersEntities.size(), usersDto.size());
        verify(userRepository).findAll();
    }

    @Test
    void testFindAll_ThrowsExceptionWhenUserNotFound() {
        when(userRepository.findAll()).thenThrow(new RuntimeException("Users not found"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.findAll());
        assertEquals("Users not found", exception.getMessage());
        verify(userRepository).findAll();
    }

    @Test
    void testDeleteUser_Success() {
        doNothing().when(userRepository).deleteById(anyLong());
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_ThrowsExceptionWhenIllegalArgument() {
        doThrow(IllegalArgumentException.class)
                .when(userRepository).deleteById(null);

        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(null));

        verify(userRepository).deleteById(null);
    }
}
