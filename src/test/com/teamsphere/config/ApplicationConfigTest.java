package com.teamsphere.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import com.teamsphere.entity.auth.User;
import com.teamsphere.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .password("password")
                .build();
    }

    @Test
    void userDetailsService_shouldReturnUserDetails_whenUserExists() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        UserDetailsService userDetailsService = applicationConfig.userDetailsService();

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    void userDetailsService_shouldThrowException_whenUserDoesNotExist() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        UserDetailsService userDetailsService = applicationConfig.userDetailsService();

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }
}