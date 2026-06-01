package com.forallergans.recepies.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // מפתח סודי קבוע להצפנה כדי למנוע ניתוקים בכל הפעלה מחדש של השרת
    private final String secretString = "mySuperSecretKeyForJwtSigning12345678901234567890";
    private final Key key = Keys.hmacShaKeyFor(secretString.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    
    // תוקף הטוקן: 24 שעות במילישניות
    private final long jwtExpirationMs = 86400000;

    // יצירת טוקן לפי שם המשתמש והתפקיד שלו
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role) // שומרים את התפקיד (USER/ADMIN) בתוך הטוקן
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    // חילוץ שם המשתמש מתוך הטוקן
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // חילוץ התפקיד מתוך הטוקן
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    // אימות שהטוקן תקין ולא פג תוקפו
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}