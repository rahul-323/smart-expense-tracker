package com.rahul.smart_expense_tracker.controller;

import com.rahul.smart_expense_tracker.dto.request.LoginRequest;
import com.rahul.smart_expense_tracker.dto.request.RegisterRequest;
import com.rahul.smart_expense_tracker.dto.response.ApiResponse;
import com.rahul.smart_expense_tracker.dto.response.AuthResponse;
import com.rahul.smart_expense_tracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest){
        AuthResponse authResponse =authService.register(registerRequest);
        return new ResponseEntity<>(ApiResponse.success("User registered successfully",authResponse), HttpStatus.CREATED);
    }
    // ─── POST /api/auth/login ───
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", authResponse)
        );
    }

}
