package com.rahul.smart_expense_tracker.service.impl;

import com.rahul.smart_expense_tracker.dto.request.LoginRequest;
import com.rahul.smart_expense_tracker.dto.request.RegisterRequest;
import com.rahul.smart_expense_tracker.dto.response.AuthResponse;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.enums.UserRole;
import com.rahul.smart_expense_tracker.exception.DuplicateResourceException;
import com.rahul.smart_expense_tracker.repository.UserRepository;
import com.rahul.smart_expense_tracker.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Unit Tests")
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .name("Rahul")
                .email("rahul@gmail.com")
                .password("password123")
                .phone("9876543210")
                .build();

        loginRequest = LoginRequest.builder()
                .email("rahul@gmail.com")
                .password("password123")
                .build();

        savedUser = User.builder()
                .userId(1L)
                .name("Rahul")
                .email("rahul@gmail.com")
                .password("hashedPassword")
                .userRole(UserRole.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtUtils.generateToken(auth)).thenReturn("jwt.token.here");

        AuthResponse result = authService.register(registerRequest);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt.token.here");
        assertThat(result.getEmail()).isEqualTo("rahul@gmail.com");
        assertThat(result.getRole()).isEqualTo("ROLE_USER");

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123"); // password was hashed
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void register_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail("rahul@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should login successfully and return token")
    void login_Success() {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtUtils.generateToken(auth)).thenReturn("jwt.login.token");
        when(userRepository.findByEmail("rahul@gmail.com")).thenReturn(Optional.of(savedUser));

        AuthResponse result = authService.login(loginRequest);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt.login.token");
        assertThat(result.getUserId()).isEqualTo("1");
    }
}
