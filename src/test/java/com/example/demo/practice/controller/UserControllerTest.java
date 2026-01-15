package com.example.demo.practice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.practice.dto.request.UserUpdateRequest;
import com.example.demo.practice.dto.response.UserResponse;
import com.example.demo.practice.entity.User;
import com.example.demo.practice.security.JwtAuthenticationFilter;
import com.example.demo.practice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(
                invocation.getArgument(0),
                invocation.getArgument(1)
            );
            return null;
        }).when(jwtAuthenticationFilter)
        .doFilter(any(), any(), any());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void getMe_authenticated_shouldReturnProfile() throws Exception {
        
        UserResponse response = UserResponse.from(mockUser("GH"));

        when(userService.getProfileByEmail("test@test.com"))
            .thenReturn(response);

        mockMvc.perform(get("/users/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@test.com"))
            .andExpect(jsonPath("$.userName").value("GH"))
            .andExpect(jsonPath("$.phone").value("0912345678"))
            .andExpect(jsonPath("$.address").value("Taipei"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void putMe_authenticated_shouldUpdateUserData() throws Exception {

        UserUpdateRequest request =
            new UserUpdateRequest("NewName", "0912345678", "Taipei");

        UserResponse response = UserResponse.from(mockUser("NewName"));

        when(userService.updateData(eq("test@test.com"), any(UserUpdateRequest.class)))
            .thenReturn(response);

        mockMvc.perform(
                put("/users/me")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userName").value("NewName"))
            .andExpect(jsonPath("$.phone").value("0912345678"))
            .andExpect(jsonPath("$.address").value("Taipei"));
    }

    @Test
    @WithMockUser(username = "test@test.com")
    void putMe_invalidRequest_shouldReturn400() throws Exception {

        UserUpdateRequest invalidRequest =
            new UserUpdateRequest("", "0999999999", "Kaohsiung");

        mockMvc.perform(
                put("/users/me")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest))
            )
            .andExpect(status().isBadRequest());
    }

    private User mockUser(String name) {
        User user = new User(
            name,
            "test@test.com",
            "0912345678",
            "Taipei",
            "password"
        );
        user.setId(1L);
        return user;
    }
}
