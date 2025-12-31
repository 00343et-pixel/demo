package com.example.demo.practice.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.practice.dto.request.UserCreateRequest;
import com.example.demo.practice.dto.request.UserUpdateRequest;
import com.example.demo.practice.dto.response.UserResponse;
import com.example.demo.practice.entity.User;
import com.example.demo.practice.exception.NotFoundException;
import com.example.demo.practice.exception.SamePasswordException;
import com.example.demo.practice.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Cacheable(value = "users", key = "'email:' + #email")
    @Transactional(readOnly = true) // 只做查詢，不應該修改資料
    public UserResponse getProfileByEmail(String email) {

        return UserResponse.from(getUser(email));
    }

    @Transactional
    @CachePut(value = "users", key = "'email:' + #result.email")
    public UserResponse createUser(UserCreateRequest request) {

        User user = new User(
            request.username(),
            request.email(),
            request.phone(),
            request.address(),
            passwordEncoder.encode(request.password()) 
        );

        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional
    public void resetPassword(String email, String rawPassword) {
        User user = getUser(email);
        if (passwordEncoder.matches(rawPassword, user.getPassword())) { //encode邏輯放service
            throw new SamePasswordException();
        }
        user.changePassword(passwordEncoder.encode(rawPassword));
    }

    @Transactional
    @CacheEvict(value = "users", key = "'email:' + #email")
    public UserResponse updateData(String email, UserUpdateRequest updateRequest) {
        User user = getUser(email);
        user.updateData(
            updateRequest.name(),
            updateRequest.phone(),
            updateRequest.address()
        );
        return UserResponse.from(user);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not exists"));
    }
}
