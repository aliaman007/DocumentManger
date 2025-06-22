
package com.main.docmanager.service;

import com.main.docmanager.model.User;
import com.main.docmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("testuser", "encodedPassword", "ROLE_USER");
        sampleUser.setId(1L);
    }

    @Test
    void testLoadUserByUsernameSuccessWithRolePrefix() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals(new SimpleGrantedAuthority("ROLE_USER"), userDetails.getAuthorities().iterator().next());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameSuccessWithoutRolePrefix() {
        sampleUser.setRole("USER");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals(new SimpleGrantedAuthority("ROLE_USER"), userDetails.getAuthorities().iterator().next());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("testuser");
        });
        assertEquals("User not found: testuser", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameNullUsername() {
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(null);
        });
        assertEquals("User not found: null", exception.getMessage());
        verify(userRepository).findByUsername(null);
    }
}
