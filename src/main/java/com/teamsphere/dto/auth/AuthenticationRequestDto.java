package com.teamsphere.dto.auth;

import jakarta.validation.constraints.Email;

public record AuthenticationRequestDto(@Email String email,
                                       String password) {

}