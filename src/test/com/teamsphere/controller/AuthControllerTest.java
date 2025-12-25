package com.teamsphere.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsphere.auth.AuthenticationResponse;
import com.teamsphere.auth.AuthenticationService;
import com.teamsphere.config.JwtAuthenticationFilter;
import com.teamsphere.dto.auth.AuthenticationRequestDto;
import com.teamsphere.dto.auth.RegisterRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequestDto registerRequest;
    private AuthenticationRequestDto authRequest;
    private AuthenticationResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDto("John", "Doe", "john.doe@example.com", "password");
        authRequest = new AuthenticationRequestDto("john.doe@example.com", "password");
        authResponse = AuthenticationResponse.builder().token("jwtToken").build();
    }

    @Test
    void register_shouldReturnToken() throws Exception {
        // Given
        when(authenticationService.register(any(RegisterRequestDto.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtToken"));
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        // Given
        when(authenticationService.authenticate(any(AuthenticationRequestDto.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtToken"));
    }
}