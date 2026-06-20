package com.rahul.smart_expense_tracker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Reads from application.properties → jwt.secret
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Reads from application.properties → jwt.expiration (in ms)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // ─── Generate the signing key from secret string ───
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // ─── Generate JWT token after successful login ───
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        assert userDetails != null;
        return Jwts.builder()
                .setSubject(userDetails.getUsername())     // email goes here
                .setIssuedAt(now)                          // token created at
                .setExpiration(expiryDate)                 // token expires at
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // sign with secret
                .compact();                                // build the token string
    }

    // ─── Extract email from token ───
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // ─── Validate token — is it valid and not expired? ───
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty");
        }
        return false;
    }
}
