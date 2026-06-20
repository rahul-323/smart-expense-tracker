package com.rahul.smart_expense_tracker.service;

import com.rahul.smart_expense_tracker.dto.request.LoginRequest;
import com.rahul.smart_expense_tracker.dto.request.RegisterRequest;
import com.rahul.smart_expense_tracker.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);

}
