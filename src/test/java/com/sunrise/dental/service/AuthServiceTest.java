package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.LoginRequest;
import com.sunrise.dental.dto.response.AuthResponse;
import com.sunrise.dental.entity.User;
import com.sunrise.dental.enums.Role;
import com.sunrise.dental.exception.UnauthorizedException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.UserRepository;
import com.sunrise.dental.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User("admin", "$2a$10$hashedAdminPassword", "System Administrator", "admin@sunrisedental.lk", Role.ADMIN);
        mockUser.setId(1L);
    }

    @Test
    @DisplayName("TC001: Successfully authenticate valid user credentials")
    void shouldAuthenticateValidUser() {
        LoginRequest request = new LoginRequest("admin", "admin123");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("admin123", "$2a$10$hashedAdminPassword")).thenReturn(true);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("admin", response.getUsername());
        assertEquals(Role.ADMIN, response.getRole());
        assertNotNull(response.getToken());
        verify(auditService, times(1)).logAction(eq("admin"), eq("LOGIN"), eq("USER"), anyString(), anyString());
    }

    @Test
    @DisplayName("TC002: Reject authentication when password is invalid")
    void shouldRejectInvalidPassword() {
        LoginRequest request = new LoginRequest("admin", "wrongPassword");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongPassword", "$2a$10$hashedAdminPassword")).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authService.login(request);
        });

        assertEquals("Invalid username or password.", exception.getMessage());
    }

    @Test
    @DisplayName("TC003: Reject authentication when username is blank")
    void shouldRejectBlankUsername() {
        LoginRequest request = new LoginRequest("", "password123");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            authService.login(request);
        });

        assertEquals("Please enter your username.", exception.getMessage());
    }
}
