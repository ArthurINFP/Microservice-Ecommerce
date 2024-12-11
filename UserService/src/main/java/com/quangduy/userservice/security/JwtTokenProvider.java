package com.quangduy.userservice.security;


import com.quangduy.userservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private Key key;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public JwtTokenProvider(@Value("${app.jwtSecret}") String jwtSecret) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }


    public String generateToken(User user) {


        Date now = new Date();
        Date expiryDate =
                new Date(now.getTime() + jwtExpirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles",user.getRole().name());
        claims.put("userId",user.getId());

        System.out.println("JWT Claims: " + claims);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256
                        )
                .compact();
    }

    public Claims getClaimsFromJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)  // Updated to use Key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)  // Updated to use Key
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (JwtException ex) {
            // Log exception or handle accordingly
            return false;
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCrypt password encoder
        return new BCryptPasswordEncoder();
    }
}

