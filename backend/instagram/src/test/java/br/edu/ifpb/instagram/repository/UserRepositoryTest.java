package br.edu.ifpb.instagram.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        @Test
        void testUpdatePartialUser() {
                var user = new UserEntity();
                user.setEmail("email_test");
                user.setFullName("fullname_test");
                user.setUsername("username_test");
                user.setEncryptedPassword("encrypted_password_test");

                var savedUser = testEntityManager.persist(user);

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
}
