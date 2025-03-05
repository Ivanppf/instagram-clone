package br.edu.ifpb.instagram.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Test
    void shouldGenerateTokenSuccessfully() {

        when(authentication.getName()).thenReturn("usuarioTeste");

        String token = jwtUtils.generateToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);

    }

    @Test
    void shouldReturnUserNameSuccessfully() {
        when(authentication.getName()).thenReturn("usuarioTeste");

        String token = jwtUtils.generateToken(authentication);

        String name = jwtUtils.getUsernameFromToken(token);

        assertNotNull(name);
        assertFalse(name.isEmpty());
        assertEquals("usuarioTeste", name);

    }

    @Test
    void shouldValidateTokenSuccessfully() {

        when(authentication.getName()).thenReturn("usuarioTeste");

        String token = jwtUtils.generateToken(authentication);

        assertTrue(jwtUtils.validateToken(token));

    }

}
