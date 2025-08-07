package mertcan.usermanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import mertcan.usermanagement.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationInSeconds;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInSeconds * 1000);
        
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        
        return Jwts.builder()
            .setSubject(userPrincipal.getUsername())
            .claim("userId", userPrincipal.getId())
            .claim("authorities", authorities)
            .claim("email", userPrincipal.getEmail())
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    public String generateTokenFromUser(User user) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInSeconds * 1000);
        
        String authorities = user.getRoles().stream()
            .map(role -> "ROLE_" + role.getName())
            .collect(Collectors.joining(","));
        
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userId", user.getId())
            .claim("authorities", authorities)
            .claim("email", user.getEmail())
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        return claims.getSubject();
    }
    
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        return claims.get("userId", Long.class);
    }
    
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        return claims.getExpiration();
    }
    
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Geçersiz JWT imzası");
        } catch (MalformedJwtException ex) {
            log.error("Geçersiz JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Süresi dolmuş JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Desteklenmeyen JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string boş");
        }
        return false;
    }
    
    public LocalDateTime getExpirationAsLocalDateTime(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
