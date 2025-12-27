package com.teamsphere.dto.auth;

import jakarta.validation.constraints.Email;

/**
 * Data Transfer Object for authentication requests.
 * Contains user email and password for login.
 *
 * @param email    user's email address
 * @param password user's password
 */
public record AuthenticationRequestDto(@Email String email,
                                       String password) {

}