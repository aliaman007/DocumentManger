
package com.main.docmanager.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginResponseTest {

    @Test
    void testDefaultConstructor() {
        LoginResponse dto = new LoginResponse();

        assertNull(dto.getToken(), "Token should be null");
        assertNull(dto.getUsername(), "Username should be null");
        assertNull(dto.getRole(), "Role should be null");
    }

    @Test
    void testParameterizedConstructor() {
        String token = "jwt.token.here";
        String username = "testuser";
      
        LoginResponse dto = new LoginResponse(token, username);

        assertEquals(token, dto.getToken(), "Token should match");
        assertEquals(username, dto.getUsername(), "Username should match");
    
    }

    @Test
    void testParameterizedConstructorWithNullValues() {
        LoginResponse dto = new LoginResponse(null, null);

        assertNull(dto.getToken(), "Token should be null");
        assertNull(dto.getUsername(), "Username should be null");
        assertNull(dto.getRole(), "Role should be null");
    }

    @Test
    void testSettersAndGetters() {
        LoginResponse dto = new LoginResponse();
        String token = "jwt.token.here";
        String username = "testuser";
        String role = "ROLE_ADMIN";

        dto.setToken(token);
        dto.setUsername(username);
        dto.setRole(role);

        assertEquals(token, dto.getToken(), "Token should match");
        assertEquals(username, dto.getUsername(), "Username should match");
        assertEquals(role, dto.getRole(), "Role should match");
    }

    @Test
    void testSettersWithNullValues() {
        LoginResponse dto = new LoginResponse();
        dto.setToken(null);
        dto.setUsername(null);
        dto.setRole(null);

        assertNull(dto.getToken(), "Token should be null");
        assertNull(dto.getUsername(), "Username should be null");
        assertNull(dto.getRole(), "Role should be null");
    }

    @Test
    void testSettersWithEmptyValues() {
        LoginResponse dto = new LoginResponse();
        dto.setToken("");
        dto.setUsername("");
        dto.setRole("");

        assertEquals("", dto.getToken(), "Token should be empty");
        assertEquals("", dto.getUsername(), "Username should be empty");
        assertEquals("", dto.getRole(), "Role should be empty");
    }
}
