package com.example.yourproject.config;

import com.example.yourproject.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtRequestFilter(UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                // System.out.println("Successfully extracted username: " + username + " from token");
            } catch (IllegalArgumentException e) {
                System.err.println("Unable to get JWT Token: " + e.getMessage());
            } catch (ExpiredJwtException e) {
                System.err.println("JWT Token has expired: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"JWT Token has expired\", \"message\": \"" + e.getMessage() + "\"}");
                return;
            } catch (Exception e) {
                // 其他JWT相关异常 (MalformedJwtException, SignatureException, etc.)
                System.err.println("JWT Token validation error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid JWT Token\", \"message\": \"" + e.getMessage() + "\"}");
                return;
            }
        } else if (requestTokenHeader != null) {
            System.err.println("JWT Token does not begin with Bearer String: " + requestTokenHeader);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            // 使用只接受token字符串的validateToken方法，并额外比较用户名
            if (jwtTokenUtil.validateToken(jwtToken) && userDetails.getUsername().equals(username)) {
                 UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                // System.out.println("Authentication successful for user: " + username);
            } else {
                System.err.println("Token validation failed for user: " + username);
            }
        }
        chain.doFilter(request, response);
    }
}