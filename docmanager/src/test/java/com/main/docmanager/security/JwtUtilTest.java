
package com.main.docmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    @Spy
    @InjectMocks
    private JwtUtil jwtUtil;

    private UserDetails userDetails;
    private String validSecret;
    private Long expiration;

    @BeforeEach
    void setUp() {
        validSecret = "thisisaverylongsecretkeythatis32bytesorlonger";
        expiration = 3600L; // 1 hour in seconds
        ReflectionTestUtils.setField(jwtUtil, "secret", validSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);

        userDetails = new org.springframework.security.core.userdetails.User(
            "testuser",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    @Test
    void testGetSigningKeySuccess() {
        Key key = ReflectionTestUtils.invokeMethod(jwtUtil, "getSigningKey");
        assertNotNull(key);
        assertEquals("HmacSHA256", key.getAlgorithm());
    }

  

    @Test
    void testGenerateTokenWithRolePrefix() {
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        assertEquals("testuser", jwtUtil.extractUsername(token));
        assertEquals("ROLE_ADMIN", jwtUtil.extractRole(token));
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testGenerateTokenWithoutRolePrefix() {
        UserDetails userWithoutRolePrefix = new org.springframework.security.core.userdetails.User(
            "testuser",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ADMIN"))
        );
        String token = jwtUtil.generateToken(userWithoutRolePrefix);
        assertNotNull(token);
        assertEquals("testuser", jwtUtil.extractUsername(token));
        assertEquals("ROLE_ADMIN", jwtUtil.extractRole(token));
    }

    @Test
    void testCreateTokenSuccess() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_ADMIN");
        String subject = "testuser";
        String token = ReflectionTestUtils.invokeMethod(jwtUtil, "createToken", claims, subject);
        assertNotNull(token);
        assertEquals(subject, jwtUtil.extractUsername(token));
        assertEquals("ROLE_ADMIN", jwtUtil.extractRole(token));
    }

    @Test
    void testCreateTokenFailure() {
        ReflectionTestUtils.setField(jwtUtil, "secret", "short");
        Map<String, Object> claims = new HashMap<>();
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ReflectionTestUtils.invokeMethod(jwtUtil, "createToken", claims, "testuser");
        });
        assertEquals("Failed to generate JWT", exception.getMessage());
    }

    @Test
    void testValidateTokenSuccess() {
        String token = jwtUtil.generateToken(userDetails);
        Boolean isValid = jwtUtil.validateToken(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void testValidateTokenInvalidUsername() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails wrongUser = new org.springframework.security.core.userdetails.User(
            "wronguser",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        Boolean isValid = jwtUtil.validateToken(token, wrongUser);
        assertFalse(isValid);
    }

    

  

    @Test
    void testExtractUsernameSuccess() {
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void testExtractUsernameInvalidToken() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractUsername("invalid.token.here");
        });
        assertEquals("Invalid JWT token", exception.getMessage());
    }

    @Test
    void testExtractRoleSuccess() {
        String token = jwtUtil.generateToken(userDetails);
        String role = jwtUtil.extractRole(token);
        assertEquals("ROLE_ADMIN", role);
    }

    @Test
    void testExtractRoleInvalidToken() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractRole("invalid.token.here");
        });
        assertEquals("Invalid JWT token", exception.getMessage());
    }

    @Test
    void testExtractClaimSuccess() {
        String token = jwtUtil.generateToken(userDetails);
        String subject = jwtUtil.extractClaim(token, Claims::getSubject);
        assertEquals("testuser", subject);
    }

    @Test
    void testExtractClaimInvalidToken() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractClaim("invalid.token.here", Claims::getSubject);
        });
        assertEquals("Invalid JWT token", exception.getMessage());
    }

    @Test
    void testExtractAllClaimsSuccess() {
        String token = jwtUtil.generateToken(userDetails);
        Object claims = ReflectionTestUtils.invokeMethod(jwtUtil, "extractAllClaims", token);
        assertNotNull(claims);
        assertEquals("testuser", ((io.jsonwebtoken.Claims) claims).getSubject());
    }

    @Test
    void testExtractAllClaimsInvalidToken() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ReflectionTestUtils.invokeMethod(jwtUtil, "extractAllClaims", "invalid.token.here");
        });
        assertEquals("Invalid JWT token", exception.getMessage());
    }

    @Test
    void testIsTokenExpiredNotExpired() {
        String token = jwtUtil.generateToken(userDetails);
        Boolean isExpired = jwtUtil.isTokenExpired(token);
        assertFalse(isExpired);
    }

 
   
}
