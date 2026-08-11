package com.satuduatiga.api.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // 24 hours

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration; // 7 days

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateToken(CustomUserDetails userDetails) {
        return buildToken(userDetails, jwtExpiration);
    }

    public String generateRefreshToken(CustomUserDetails userDetails) {
        return buildToken(userDetails, refreshExpiration);
    }

    public String buildToken(CustomUserDetails userDetails, long expiration) {
        return Jwts.builder()
                .subject(userDetails.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .claim("roles", userDetails.getAuthorities())
                .signWith(getSigningKey())
                .compact();
    }

    public String extractSubject(String token) {
        return extractClaim(token, claim -> claim.getSubject());
    }

    public Authentication getAuthentication(String token, CustomUserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, claim -> claim.getExpiration()).before(new Date());
    }

    public boolean isTokenValid(String token, CustomUserDetails userDetails) {
        String subject = extractSubject(token);
        return subject.equals(userDetails.getEmail()) && !isTokenExpired(token);
    }

    public Long getExpirationTime() {
        return jwtExpiration;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claimsResolver.apply(claims);
    }
}
