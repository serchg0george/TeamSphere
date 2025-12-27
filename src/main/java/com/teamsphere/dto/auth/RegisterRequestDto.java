package com.teamsphere.dto.auth;

import jakarta.validation.constraints.Email;

/**
 * Data Transfer Object for user registration requests.
 * Contains user details for creating a new account.
 *
 * @param firstName user's first name
 * @param lastName  user's last name
 * @param email     user's email address
 * @param password  user's password
 */
public record RegisterRequestDto(String firstName,
                                 String lastName,
                                 @Email String email,
                                 String password) {

}
