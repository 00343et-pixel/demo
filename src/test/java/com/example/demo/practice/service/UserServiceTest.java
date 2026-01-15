package com.example.demo.practice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.practice.dto.request.UserCreateRequest;
import com.example.demo.practice.dto.request.UserUpdateRequest;
import com.example.demo.practice.dto.response.UserResponse;
import com.example.demo.practice.entity.User;
import com.example.demo.practice.exception.NotFoundException;
import com.example.demo.practice.exception.SamePasswordException;
import com.example.demo.practice.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void getProfileByEmail_userExists_shouldReturnProfile() {
        
        User user = new User(
            "GH",
            "test@test.com",
            "0912345678",
            "Taipei",
            "encodedPwd"
        );

        when(userRepository.findByEmail("test@test.com"))
            .thenReturn(Optional.of(user));

        UserResponse response =
            userService.getProfileByEmail("test@test.com");

        assertEquals("GH", response.userName());
        assertEquals("test@test.com", response.email());
    }

    @Test
    void getProfileByEmail_userNotExist_shouldThrowNotFound() {
        
        when(userRepository.findByEmail("test@test.com"))
            .thenReturn(Optional.empty());

        assertThrows(
            NotFoundException.class,
            () -> userService.getProfileByEmail("test@test.com")
        );
    }

    @Test
    void createUser_shouldEncodePasswordAndSaveUser() {
        
        UserCreateRequest request = new UserCreateRequest(
            "GH",
            "rawPwd",
            "test@test.com",
            "0912345678",
            "Taipei"
        );

        when(passwordEncoder.encode("rawPwd"))
            .thenReturn("encodedPwd");

        UserResponse response = userService.createUser(request);

        assertEquals("test@test.com", response.email());

        verify(passwordEncoder).encode("rawPwd");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void resetPassword_samePassword_shouldThrowException() {

        User user = new User(
            "GH",
            "test@test.com",
            "0912345678",
            "Taipei",
            "encodedPwd"
        );

        when(userRepository.findByEmail("test@test.com"))
            .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("rawPwd", "encodedPwd"))
            .thenReturn(true);

        assertThrows(
            SamePasswordException.class,
            () -> userService.resetPassword("test@test.com", "rawPwd")
        );
    }

    @Test
    void resetPassword_differentPassword_shouldUpdatePassword() {

        User user = new User(
            "GH",
            "test@test.com",
            "0912345678",
            "Taipei",
            "oldEncodedPwd"
        );

        when(userRepository.findByEmail("test@test.com"))
            .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("newPwd", "oldEncodedPwd"))
            .thenReturn(false);

        when(passwordEncoder.encode("newPwd"))
            .thenReturn("newEncodedPwd");

        userService.resetPassword("test@test.com", "newPwd");

        verify(passwordEncoder).encode("newPwd");
    }

    @Test
    void updateData_shouldUpdateUserFields() {

        User user = new User(
            "GH",
            "test@test.com",
            "0912345678",
            "Taipei",
            "encodedPwd"
        );

        UserUpdateRequest request =
            new UserUpdateRequest("NewName", "0999999999", "Taichung");

        when(userRepository.findByEmail("test@test.com"))
            .thenReturn(Optional.of(user));

        UserResponse response =
            userService.updateData("test@test.com", request);

        assertEquals("NewName", response.userName());
        assertEquals("0999999999", response.phone());
    }
}
