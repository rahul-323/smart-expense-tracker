package com.rahul.smart_expense_tracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // This method runs BEFORE every single API request
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Step 1: Extract token from "Authorization: Bearer <token>" header
        String token = getTokenFromRequest(request);

        // Step 2: If token exists and is valid
        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {

            // Step 3: Get email from token
            String email = jwtUtils.getEmailFromToken(token);

            // Step 4: Load user details from database
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            // Step 5: Create authentication object
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,                // principal (who)
                            null,                       // credentials (not needed, token is proof)
                            userDetails.getAuthorities() // roles
                    );

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // Step 6: Set authentication in Spring Security context
            // Now Spring Security knows: "This request is from an authenticated user"
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Step 7: Continue to the next filter / controller
        filterChain.doFilter(request, response);
    }

    // Helper: Extract "Bearer <token>" from Authorization header
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Remove "Bearer " prefix
        }
        return null;
    }
}
