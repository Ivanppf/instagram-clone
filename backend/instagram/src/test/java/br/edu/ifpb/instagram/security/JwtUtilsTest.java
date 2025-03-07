package br.edu.ifpb.instagram.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.RedisSessionProperties.ConfigureAction;
import org.springframework.boot.logging.LoggerConfiguration.ConfigurationScope;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

@SpringBootTest
public class JwtUtilsTest {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

    @Mock
    Authentication authentication;

    String token;

    @BeforeEach
    void setup() {
        when(authentication.getName()).thenReturn("usuarioTeste");
        token = jwtUtils.generateToken(authentication);
    }

    @Test
    void shouldGenerateTokenSuccessfully() {

        assertNotNull(token);
        assertFalse(token.isEmpty());
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);

    }

    @Test
    void shouldReturnUserNameSuccessfully() {

        String name = jwtUtils.getUsernameFromToken(token);

        assertNotNull(name);
        assertFalse(name.isEmpty());
        assertEquals("usuarioTeste", name);

    }

    @Test
    void tokenShouldBeValid() {

        assertTrue(jwtUtils.validateToken(token));

    }

    @Test
    void tokenShouldNotBeValid() {

        String newToken = token + "a";
        assertFalse(jwtUtils.validateToken(newToken));

    }

}
