package com.rahul.expensetracker.service;

import com.rahul.expensetracker.dto.request.LoginRequest;
import com.rahul.expensetracker.dto.request.RegisterRequest;
import com.rahul.expensetracker.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);

}
