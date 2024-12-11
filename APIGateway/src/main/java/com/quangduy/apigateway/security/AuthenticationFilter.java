package com.quangduy.apigateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;
import java.security.Key;
import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final Key key;

    public AuthenticationFilter(@Value("${app.jwtSecret}") String jwtSecret) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip authentication for login and signup endpoints
        if (path.startsWith("/user/auth/")) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest());

        if (token == null) {
            System.out.println("JWT Token is null");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("Parsed JWT Claims: " + claims);

            String userId = String.valueOf(claims.get("userId"));
            String email = claims.getSubject();
            String roles = String.valueOf(claims.get("roles"));

            // Add user info to headers
            exchange = exchange.mutate().request(exchange.getRequest().mutate()
                    .header("X-User-ID", userId)
                    .header("X-User-Email", email)
                    .header("X-User-Roles", roles)
                    .build()).build();

            return chain.filter(exchange);

        } catch (JwtException ex) {
            System.err.println("JWT Parsing Exception: " + ex.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private String extractToken(ServerHttpRequest request) {
        List<String> authHeaders = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);
        if (!authHeaders.isEmpty()) {
            String bearerToken = authHeaders.get(0);
            if (bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

