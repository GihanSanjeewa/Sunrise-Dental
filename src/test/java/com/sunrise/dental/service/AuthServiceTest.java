package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.LoginRequest;
import com.sunrise.dental.dto.request.UserCreateRequest;
import com.sunrise.dental.dto.request.UserUpdateRequest;
import com.sunrise.dental.dto.response.AuthResponse;
import com.sunrise.dental.dto.response.UserResponse;
import com.sunrise.dental.entity.User;
import com.sunrise.dental.enums.Permission;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        mockUser.setPermissions(new HashSet<>(Permission.getDefaultPermissions(Role.ADMIN)));
    }

    @Test
    @DisplayName("TC001: Successfully authenticate valid user credentials and return permissions")
    void shouldAuthenticateValidUser() {
        LoginRequest request = new LoginRequest("admin", "admin123");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("admin123", "$2a$10$hashedAdminPassword")).thenReturn(true);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("admin", response.getUsername());
        assertEquals(Role.ADMIN, response.getRole());
        assertNotNull(response.getToken());
        assertNotNull(response.getPermissions());
        assertTrue(response.getPermissions().contains("USER_MANAGE"));
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

    @Test
    @DisplayName("TC004: Create new staff user with custom permissions")
    void shouldCreateUserWithCustomPermissions() {
        Set<String> customPerms = Set.of("PATIENT_READ", "APPOINTMENT_READ", "INVENTORY_READ");
        UserCreateRequest request = new UserCreateRequest(
                "recept.sam", "secret123", "Samantha Silva", "samantha@sunrisedental.lk",
                Role.RECEPTIONIST, customPerms
        );

        when(userRepository.existsByUsername("recept.sam")).thenReturn(false);
        when(userRepository.existsByEmail("samantha@sunrisedental.lk")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(5L);
            return u;
        });

        UserResponse response = authService.createUser(request);

        assertNotNull(response);
        assertEquals("recept.sam", response.getUsername());
        assertEquals(Role.RECEPTIONIST, response.getRole());
        assertTrue(response.getPermissions().contains("INVENTORY_READ"));
        assertTrue(response.getPermissions().contains("PATIENT_READ"));
        verify(auditService, times(1)).logAction(eq("ADMIN"), eq("CREATE_USER"), eq("USER"), eq("5"), anyString());
    }

    @Test
    @DisplayName("TC005: Update staff user profile, role, and permissions")
    void shouldUpdateUserAndPermissions() {
        User existingUser = new User("recept.kamani", "$2a$10$encoded", "Kamani", "kamani@sunrisedental.lk", Role.RECEPTIONIST);
        existingUser.setId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("kamani.new@sunrisedental.lk")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Set<String> updatedPerms = Set.of("PATIENT_READ", "PATIENT_WRITE", "BILLING_READ", "BILLING_WRITE", "REPORTS_VIEW");
        UserUpdateRequest request = new UserUpdateRequest("Kamani Jayawardena", "kamani.new@sunrisedental.lk", Role.RECEPTIONIST, true, updatedPerms);

        UserResponse response = authService.updateUser(2L, request);

        assertNotNull(response);
        assertEquals("Kamani Jayawardena", response.getFullName());
        assertEquals("kamani.new@sunrisedental.lk", response.getEmail());
        assertTrue(response.getPermissions().contains("BILLING_WRITE"));
        verify(auditService, times(1)).logAction(eq("ADMIN"), eq("UPDATE_USER"), eq("USER"), eq("2"), anyString());
    }

    @Test
    @DisplayName("TC006: Unlock locked user account and reset failed login attempts")
    void shouldUnlockUserAccount() {
        User lockedUser = new User("locked.user", "$2a$10$encoded", "Locked User", "locked@sunrisedental.lk", Role.RECEPTIONIST);
        lockedUser.setId(10L);
        lockedUser.setFailedLoginAttempts(5);
        lockedUser.setLockoutUntil(LocalDateTime.now().plusMinutes(15));

        when(userRepository.findById(10L)).thenReturn(Optional.of(lockedUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.unlockUser(10L);

        assertEquals(0, lockedUser.getFailedLoginAttempts());
        assertNull(lockedUser.getLockoutUntil());
        verify(auditService, times(1)).logAction(eq("ADMIN"), eq("UNLOCK_USER"), eq("USER"), eq("10"), anyString());
    }

    @Test
    @DisplayName("TC007: Admin securely reset user password")
    void shouldAdminResetPassword() {
        User targetUser = new User("user.target", "$2a$10$oldHash", "Target User", "target@sunrisedental.lk", Role.DENTIST);
        targetUser.setId(8L);

        when(userRepository.findById(8L)).thenReturn(Optional.of(targetUser));
        when(passwordEncoder.encode("NewSecurePass123!")).thenReturn("$2a$10$newEncodedHash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.adminResetPassword(8L, "NewSecurePass123!");

        assertEquals("$2a$10$newEncodedHash", targetUser.getPasswordHash());
        assertEquals(0, targetUser.getFailedLoginAttempts());
        assertNull(targetUser.getLockoutUntil());
        verify(auditService, times(1)).logAction(eq("ADMIN"), eq("ADMIN_RESET_PASSWORD"), eq("USER"), eq("8"), anyString());
    }
}
