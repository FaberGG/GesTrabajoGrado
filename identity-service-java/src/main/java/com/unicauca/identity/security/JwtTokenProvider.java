package com.unicauca.identity.security;

import com.unicauca.identity.entity.User;
import com.unicauca.identity.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Proveedor de tokens JWT para autenticación
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    /**
     * Genera un token JWT para un usuario autenticado
     *
     * @param user El usuario para el que se genera el token
     * @return Token JWT generado
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("rol", user.getRol().toString())
                .claim("programa", user.getPrograma().toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Obtiene el email del usuario desde un token JWT
     *
     * @param token Token JWT a verificar
     * @return Email del usuario
     */
    public String getUserEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Obtiene el ID de usuario desde un token JWT
     *
     * @param token Token JWT a verificar
     * @return ID del usuario
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * Valida un token JWT
     *
     * @param token Token JWT a validar
     * @return true si el token es válido
     * @throws InvalidTokenException si el token es inválido
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Firma JWT inválida");
            throw new InvalidTokenException("Firma JWT inválida");
        } catch (MalformedJwtException ex) {
            log.error("Token JWT inválido");
            throw new InvalidTokenException("Token JWT inválido");
        } catch (ExpiredJwtException ex) {
            log.error("Token JWT expirado");
            throw new InvalidTokenException("Token JWT expirado");
        } catch (UnsupportedJwtException ex) {
            log.error("Token JWT no soportado");
            throw new InvalidTokenException("Token JWT no soportado");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims vacío");
            throw new InvalidTokenException("JWT claims vacío");
        }
    }

    /**
     * Obtiene todos los claims (datos) de un token JWT
     *
     * @param token Token JWT a analizar
     * @return Claims del token
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Genera la clave de firma para los tokens JWT a partir del secreto configurado
     *
     * @return Clave de firma
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
