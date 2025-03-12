package br.edu.ifpb.instagram.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import br.edu.ifpb.instagram.model.entity.UserEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryTest {

        @Autowired
        UserRepository userRepository;

        @Autowired
        TestEntityManager testEntityManager;

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

                userEntities.add(testEntityManager.persist(user1));
                userEntities.add(testEntityManager.persist(user2));
                userEntities.add(testEntityManager.persist(user3));
        }

        @AfterEach
        void tearDown() {
                userEntities.removeAll(userEntities);
                testEntityManager.flush();
                testEntityManager.clear();
        }

        @Test
        void testUpdatePartialUser() {
                var savedUser = userEntities.get(0);

                int updatedUser = userRepository.updatePartialUser(null, "updated_email_test", null, null,
                                savedUser.getId());

                assertEquals(1, updatedUser);
        }

        @Test
        void testSaveUser() {
                var user = new UserEntity();
                user.setEmail("email_test");
                user.setFullName("fullname_test");
                user.setUsername("username_test");
                user.setEncryptedPassword("encrypted_password_test");

                var savedUser = userRepository.save(user);

                assertNotNull(savedUser);
                assertNotNull(savedUser.getId());
                assertEquals("username_test", savedUser.getUsername());
        }

        @Test
        void testFindAll() {
                List<UserEntity> users = userRepository.findAll();
                assertEquals(3, users.size());
        }

        @Test
        void testFindByUsername_ReturnOptionalNotEmpty() {
                String username = userEntities.get(0).getUsername();
                Optional<UserEntity> userOptional = userRepository.findByUsername(username);
                assertTrue(userOptional.isPresent());
                assertEquals(username, userOptional.get().getUsername());
        }

        @Test
        void testFindByUsername_ReturnOptionalEmpty() {
                Optional<UserEntity> userOptional = userRepository.findByUsername("atosalves");
                assertTrue(userOptional.isEmpty());
        }

        @Test
        void testDeleteById_Successful() {
                Long id = userEntities.get(1).getId();
                userRepository.deleteById(id);
                UserEntity user = testEntityManager.find(UserEntity.class, id);
                assertNull(user);
        }

        @Test
        void testDeleteById_Unsuccessful() {
                Long id = 20L;
                userRepository.deleteById(id);
                var list = userRepository.findAll();
                assertEquals(userEntities.size(), list.size());
        }

        @Test
        void testFindById_ReturnOptionalNotEmpty() {
                Long id = userEntities.get(0).getId();
                Optional<UserEntity> user = userRepository.findById(id);
                assertTrue(user.isPresent());
                assertEquals(userEntities.get(0), user.get());
        }

        @Test
        void testFindById_ReturnOptionalEmpty() {
                Long id = 20L;
                Optional<UserEntity> user = userRepository.findById(id);
                assertTrue(user.isEmpty());
        }
}
