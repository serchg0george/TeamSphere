package com.teamsphere.auth;

import com.teamsphere.config.JwtService;
import com.teamsphere.dto.auth.AuthenticationRequestDto;
import com.teamsphere.dto.auth.RegisterRequestDto;
import com.teamsphere.entity.auth.Role;
import com.teamsphere.entity.auth.User;
import com.teamsphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for user authentication and registration operations.
 * Handles JWT token generation and user credential validation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user in the system.
     *
     * @param request the registration request containing user details
     * @return AuthenticationResponse containing the JWT token for the newly registered user
     */
    public AuthenticationResponse register(RegisterRequestDto request) {
        log.info("Registering new user: {}", request.email());
        var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);

        log.info("User {} registered successfully", request.email());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Authenticates an existing user with their credentials.
     *
     * @param request the authentication request containing email and password
     * @return AuthenticationResponse containing the JWT token for the authenticated user
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     */
    public AuthenticationResponse authenticate(AuthenticationRequestDto request) {
        log.info("Authenticating user: {}", request.email());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = repository.findByEmail(request.email()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        log.info("User {} authenticated successfully", request.email());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
