package com.teamsphere.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.teamsphere.config.JwtService;
import com.teamsphere.dto.auth.AuthenticationRequestDto;
import com.teamsphere.dto.auth.RegisterRequestDto;
import com.teamsphere.entity.auth.Role;
import com.teamsphere.entity.auth.User;
import com.teamsphere.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequestDto registerRequest;
    private AuthenticationRequestDto authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDto("John", "Doe", "john.doe@example.com", "password");
        authRequest = new AuthenticationRequestDto("john.doe@example.com", "password");

        user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void register_shouldSaveUserAndReturnToken() {
        // Given
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        // When
        AuthenticationResponse response = authenticationService.register(registerRequest);

        // Then
        verify(userRepository).save(any(User.class));
        assertNotNull(response.getToken());
        assertEquals("jwtToken", response.getToken());
    }

    @Test
    void authenticate_shouldAuthenticateUserAndReturnToken() {
        // Given
        when(userRepository.findByEmail(authRequest.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        // When
        AuthenticationResponse response = authenticationService.authenticate(authRequest);

        // Then
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password())
        );
        assertNotNull(response.getToken());
        assertEquals("jwtToken", response.getToken());
    }
}