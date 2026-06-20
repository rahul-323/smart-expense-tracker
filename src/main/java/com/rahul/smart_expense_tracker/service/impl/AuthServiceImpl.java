package com.rahul.smart_expense_tracker.service.impl;

import com.rahul.smart_expense_tracker.dto.request.LoginRequest;
import com.rahul.smart_expense_tracker.dto.request.RegisterRequest;
import com.rahul.smart_expense_tracker.dto.response.AuthResponse;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.enums.UserRole;
import com.rahul.smart_expense_tracker.exception.DuplicateResourceException;
import com.rahul.smart_expense_tracker.repository.UserRepository;
import com.rahul.smart_expense_tracker.security.JwtUtils;
import com.rahul.smart_expense_tracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    // ─── REGISTER ───
    @Override
    public AuthResponse register(RegisterRequest request) {

        // Step 1: Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Step 2: Build User entity from request
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // Hash password!
                .phone(request.getPhone())
                .userRole(UserRole.ROLE_USER)    // Default role
                .monthlyIncome(request.getMonthlyIncome())
                .currency("INR")
                .build();

        // Step 3: Save to database
        User savedUser = userRepository.save(user);

        // Step 4: Auto-login after registration — generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()   // Original password (not hashed)
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateToken(authentication);

        // Step 5: Build and return response
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(String.valueOf(savedUser.getUserId()))
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getUserRole().name())
                .build();
    }

    // ─── LOGIN ───
    @Override
    public AuthResponse login(LoginRequest request) {

        // Step 1: Authenticate (Spring Security verifies email + password)
        // If wrong credentials → throws BadCredentialsException automatically
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Step 2: Set authentication in context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Step 3: Generate JWT token
        String token = jwtUtils.generateToken(authentication);

        // Step 4: Get user details for response
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Step 5: Build and return response
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(String.valueOf(user.getUserId()))
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getUserRole().name())
                .build();
    }

}
