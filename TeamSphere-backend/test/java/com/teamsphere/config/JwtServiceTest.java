package com.teamsphere.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;
    private final String secretKey = "dGhpcy1pcy1hLXZlcnktc2VjdXJlLXNlY3JldC1rZXktZm9yLXRlc3RpbmctcHVycG9zZXMtb25seQo=";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "key", secretKey);
        ReflectionTestUtils.setField(jwtService, "expirationTime", Duration.ofHours(1));
        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        // When
        String token = jwtService.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertEquals(userDetails.getUsername(), jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() {
        // Given
        ReflectionTestUtils.setField(jwtService, "expirationTime", Duration.ofMillis(1));
        String token = jwtService.generateToken(userDetails);

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_shouldReturnFalseForDifferentUser() {
        // Given
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUserDetails = new User("otheruser", "password", Collections.emptyList());

        // When
        boolean isValid = jwtService.isTokenValid(token, otherUserDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    void extractClaim_shouldExtractCorrectClaim() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        String username = jwtService.extractClaim(token, Claims::getSubject);

        // Then
        assertEquals(userDetails.getUsername(), username);
    }
}