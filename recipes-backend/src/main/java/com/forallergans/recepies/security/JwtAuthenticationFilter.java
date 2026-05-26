package com.forallergans.recepies.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. שולפים את ה-Header של האבטחה
        String authHeader = request.getHeader("Authorization");

        // 2. בודקים שהטוקן מתחיל בפורמט הסטנדרטי "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 3. אם הטוקן תקין, מחלצים נתונים ומאמתים מול Spring
            if (jwtUtils.validateToken(token)) {
                String username = jwtUtils.getUsernameFromToken(token);
                String role = jwtUtils.getRoleFromToken(token); // מקבלים ROLE_USER או ROLE_ADMIN

                // יוצרים אובייקט אימות עבור הזיכרון של Spring Security
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );

                // מעדכנים את ה-Context הגלובלי של האפליקציה שהמשתמש מחובר בצורה מאובטחת!
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // ממשיכים הלאה בשרשרת הטיפול בבקשה
        filterChain.doFilter(request, response);
    }
}