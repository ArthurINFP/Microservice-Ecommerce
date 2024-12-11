package com.quangduy.userservice;


import com.quangduy.userservice.dto.LoginRequest;
import com.quangduy.userservice.dto.SignupRequest;
import com.quangduy.userservice.dto.UpdateRequest;
import com.quangduy.userservice.entity.Role;
import com.quangduy.userservice.entity.User;
import com.quangduy.userservice.repository.UserRepository;
import com.quangduy.userservice.security.JwtTokenProvider;
import com.quangduy.userservice.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Test methods go here
    @Test
    void registerUser_Success() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFullName("John Doe");
        signupRequest.setEmail("john@example.com");
        signupRequest.setAddress("123 Main St");
        signupRequest.setPassword("password123");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userService.registerUser(signupRequest,false);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("John Doe", savedUser.getFullName());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("123 Main St", savedUser.getAddress());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertSame(savedUser.getRole(), Role.ROLE_USER);
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("john@example.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(signupRequest,false);
        });

        assertEquals("Email is already in use!", exception.getMessage());
    }

    @Test
    void authenticateUser_Success() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(tokenProvider.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        String token = userService.authenticateUser(loginRequest);

        // Assert
        assertEquals("jwtToken", token);
    }

    @Test
    void authenticateUser_InvalidCredentials() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.authenticateUser(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }


    @Test
    void updateUserProfile_UserNotFound() {
        // Arrange
        Long userId = 1L;
        UpdateRequest updateRequest = new UpdateRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserProfile(userId, updateRequest);
        });

        assertEquals("User not found", exception.getMessage());
    }


}
