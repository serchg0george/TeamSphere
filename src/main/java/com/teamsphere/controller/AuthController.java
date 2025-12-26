package com.teamsphere.controller;

import com.teamsphere.auth.AuthenticationResponse;
import com.teamsphere.auth.AuthenticationService;
import com.teamsphere.dto.auth.AuthenticationRequestDto;
import com.teamsphere.dto.auth.RegisterRequestDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication operations.
 * Handles user registration and login endpoints.
 */
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/auth")
@Validated
public class AuthController {

    private final AuthenticationService service;

    /**
     * Registers a new user in the system.
     *
     * @param request the registration request containing user details
     * @return ResponseEntity containing the authentication response with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequestDto request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    /**
     * Authenticates a user with their credentials.
     *
     * @param request the authentication request containing email and password
     * @return ResponseEntity containing the authentication response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequestDto request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

}
