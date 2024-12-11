package com.quangduy.userservice;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.quangduy.userservice.controller.UserProfileControler;
import com.quangduy.userservice.dto.*;
import com.quangduy.userservice.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileControler.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UpdateRequest updateRequest;
    private UserProfileResponse userProfileResponse;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        updateRequest = new UpdateRequest();
        updateRequest.setFullName("Jane Doe");
        updateRequest.setAddress("456 Elm St");
        updateRequest.setImageUrl("image.jpg");

        userProfileResponse = new UserProfileResponse();
        userProfileResponse.setId(1L);
        userProfileResponse.setFullName("Jane Doe");
        userProfileResponse.setAddress("456 Elm St");
        userProfileResponse.setImageUrl("image.jpg");
    }

    @Test
    void getCurrentUser_Success() throws Exception {
        when(userService.getCurrentUser(1L)).thenReturn(userProfileResponse);

        mockMvc.perform(get("/profile/me")
                        .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));
    }

    @Test
    void getCurrentUser_UserNotFound() throws Exception {
        doThrow(new IllegalArgumentException("User not found"))
                .when(userService).getCurrentUser(1L);

        mockMvc.perform(get("/profile/me")
                        .header("X-User-ID", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.status").value("400"));
    }

    @Test
    void updateCurrentUser_Success() throws Exception {
        when(userService.updateUserProfile(eq(1L), any(UpdateRequest.class))).thenReturn(userProfileResponse);

        mockMvc.perform(put("/profile/me")
                        .header("X-User-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));
    }

    @Test
    void updateCurrentUser_UserNotFound() throws Exception {
        doThrow(new IllegalArgumentException("User not found"))
                .when(userService).updateUserProfile(eq(1L), any(UpdateRequest.class));

        mockMvc.perform(put("/profile/me")
                        .header("X-User-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.status").value("400"));
    }

}
