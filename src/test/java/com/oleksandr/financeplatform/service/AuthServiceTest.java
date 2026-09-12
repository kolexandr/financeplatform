package com.oleksandr.financeplatform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oleksandr.financeplatform.dto.auth.AuthResponse;
import com.oleksandr.financeplatform.dto.auth.LoginRequest;
import com.oleksandr.financeplatform.dto.auth.RegisterUserRequest;
import com.oleksandr.financeplatform.entity.User;
import com.oleksandr.financeplatform.exception.EmailAlreadyExistsException;
import com.oleksandr.financeplatform.exception.InvalidCredentialsException;
import com.oleksandr.financeplatform.repository.UserRepository;
import com.oleksandr.financeplatform.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerSavesNormalizedEmailAndHashedPassword() {
        RegisterUserRequest request = new RegisterUserRequest(" USER@example.com ", "password123");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("user@example.com");
        assertThat(userCaptor.getValue().getPasswordHash()).isNotEqualTo("password123");
        assertThat(response).isEqualTo(new AuthResponse("user@example.com", "token"));
    }

    @Test
    void registerRejectsExistingEmail() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterUserRequest("user@example.com", "password123")))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void loginRejectsInvalidPassword() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPasswordHash(new BCryptPasswordEncoder().encode("correct-password"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("user@example.com", "wrong-password")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
