package org.bitebuilders.service;

import io.jsonwebtoken.*;
import org.bitebuilders.enums.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secretKey;

    @Value("${security.jwt.expiration}")
    private long expirationTime;

    @Value("${security.jwt.refresh-expiration}")
    private long refreshExpirationTime;

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
    }

    private String generateToken(String username, String role, long expiration) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role) // Здесь добавляется роль
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateToken(String username, String role) {
        return generateToken(username, role, expirationTime);
    }

    public String generateRefreshToken(String username, String role) {
        return generateToken(username, role, refreshExpirationTime);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false; // Возвращаем false для некорректных токенов
        }
    }

    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        String role = getRoleFromToken(token); // Извлекаем роль из токена

        UserDetails userDetails = User.builder()
                .username(username)
                .password("") // Пароль не нужен для авторизации
                .authorities(new SimpleGrantedAuthority("ROLE_" + role)) // Добавляем роль
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class); // Извлечение кастомного claim "role"
    }
}
