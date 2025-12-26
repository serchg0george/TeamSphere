package com.teamsphere.auth;

import lombok.*;

/**
 * Response object containing authentication token.
 * Returned after successful user registration or authentication.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    /**
     * JWT token for authenticated user.
     */
    private String token;
}
