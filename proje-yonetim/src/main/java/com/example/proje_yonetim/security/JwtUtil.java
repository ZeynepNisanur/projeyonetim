package com.example.proje_yonetim.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;
//import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    // 256 bitlik 32 karakterlik(olmak zorundaymış yoksam patlaarr O_O) gizli keyim!
    private final String SECRET = "senin-super-gizli-ve-en-az-256-bit-olmasi-gereken-secret-keyin";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 100; // 100 saat

    private Key getSigningKey() {
        return key;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    /*
     * public String extractUseradi(String token) {
     * return Jwts.parserBuilder()
     * .setSigningKey(getSigningKey())
     * .build()
     * .parseClaimsJws(token)
     * .getBody()
     * .getSubject();
     * }
     */

    public String extractUseradi(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (Exception e) {
            System.out.println("Kullanıcı adı çıkarılamadı: " + e.getMessage());
            return null;
        }
    }

    /*
     * public boolean validateToken(String token, UserDetails userDetails) {
     * final String extractedUsername = extractUseradi(token);
     * return (extractedUsername.equals(userDetails.getUsername()) &&
     * !isTokenExpired(token));
     * }
     */

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String extractedUsername = extractUseradi(token);
            return (extractedUsername != null && extractedUsername.equals(userDetails.getUsername())
                    && !isTokenExpired(token));
        } catch (Exception e) {
            System.out.println("Token doğrulanamadı: " + e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    /*
     * private Claims extractAllClaims(String token) {
     * return Jwts
     * .parserBuilder()
     * .setSigningKey(getSigningKey())
     * .build()
     * .parseClaimsJws(token)
     * .getBody();
     * }
     */

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("Token süresi dolmuş: " + e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            System.out.println("Token yapısı bozuk: " + e.getMessage());
            throw e;
        } catch (SignatureException e) {
            System.out.println("Token imzası geçersiz: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Token çözümlenirken hata oluştu: " + e.getMessage());
            throw e;
        }
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 100 * 24 * 60 * 60 * 1000)) // 100 gün
                .signWith(key)
                .compact();
    }

}