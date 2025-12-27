package com.teamsphere.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for handling JWT token operations including generation, validation, and claim extraction.
 * Uses HMAC-SHA256 algorithm for token signing.
 */
@Slf4j
@Service
public class JwtService {

    @Value("${SECRET_KEY}")
    private String key;

    @Value("${jwt.expiration-time}")
    Duration expirationTime;

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUsername(String token) {
        log.info("Extracting username from token");
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token using the provided claims resolver function.
     *
     * @param <T>            the type of the claim to extract
     * @param token          the JWT token
     * @param claimsResolver function to extract the desired claim from the claims
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token the JWT token
     * @return all claims contained in the token
     */
    private Claims extractAllClaims(String token) {
        log.info("Extracting all claims from token");
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates the signing key from the base64-encoded secret key.
     *
     * @return the signing key for JWT operations
     */
    private Key getSignInKey() {
        log.info("Generating signing key");
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for the given user details without extra claims.
     *
     * @param userDetails the user details
     * @return the generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user: {}", userDetails.getUsername());
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token for the given user details with additional claims.
     *
     * @param extraClaims additional claims to include in the token
     * @param userDetails the user details
     * @return the generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        log.info("Generating token with extra claims for user: {}", userDetails.getUsername());
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(expirationTime)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the JWT token against the provided user details.
     * Checks if the token's username matches and if the token is not expired.
     *
     * @param token       the JWT token to validate
     * @param userDetails the user details to validate against
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
            log.info("Token validation result for user {}: {}", userDetails.getUsername(), isValid);
            return isValid;
        } catch (io.jsonwebtoken.JwtException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        try {
            boolean expired = extractExpiration(token).before(new Date());
            log.info("Token expired: {}", expired);
            return expired;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.info("Token expired: true");
            return true;
        }
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration date of the token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
