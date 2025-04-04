package com.bookonthego.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.function.Function;

@Service
public class JwtUtil {
    // Getter method to access the secret key
    // Private secret key used for signing JWT tokens
    private final String secretKey = "wgyOEvtiUstJKYCQFRtj35fUweCPbQ89PsyjxVNtle4=";

    private SecretKey getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    public Claims extractAllClaims(String jwtToken) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(jwtToken).getPayload();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    public String extractUserId(String jwtToken) {
        return extractClaims(jwtToken, Claims::getId);
    }


    // Method to generate a JWT token with userId as the subject
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId) // Set userId as the subject
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, secretKey) // Sign the token with secretKey
                .compact();
    }

    // Method to validate the JWT token
    public boolean validateToken(String token, @org.jetbrains.annotations.NotNull String userId) {
        String extractedUserId = extractUserId(token);
        return (userId.equals(extractedUserId));
    }
}
