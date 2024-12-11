package com.quangduy.userservice;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangduy.userservice.controller.AuthController;
import com.quangduy.userservice.dto.*;
import com.quangduy.userservice.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setFullName("John Doe");
        signupRequest.setEmail("john@example.com");
        signupRequest.setAddress("123 Main St");
        signupRequest.setPassword("password123");
        signupRequest.setPhone("0909090909");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void registerUser_Success() throws Exception {
        doNothing().when(userService).registerUser(any(SignupRequest.class),anyBoolean());


        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))

//                        .content("{\"email\":\"john@example.com\",\"fullName\":\"John Doe\",\"address\":\"123 Main St\",\"password\":\"password123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void registerUser_EmailAlreadyExists() throws Exception {
        doThrow(new IllegalArgumentException("Email is already in use!"))
                .when(userService).registerUser(any(SignupRequest.class),anyBoolean());

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Email is already in use!"))
                .andExpect(jsonPath("$.status").value("400"));
    }


    @Test
    void authenticateUser_Success() throws Exception {
        when(userService.authenticateUser(any(LoginRequest.class))).thenReturn("jwtToken");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("jwtToken"));
    }

    @Test
    void authenticateUser_InvalidCredentials() throws Exception {
        doThrow(new IllegalArgumentException("Invalid email or password"))
                .when(userService).authenticateUser(any(LoginRequest.class));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.status").value("400"));
    }

}
