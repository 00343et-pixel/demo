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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Cacheable(value = "users", key = "'email:' + #email")
    @Transactional(readOnly = true)
    public UserResponse getProfileByEmail(String email) {

        return UserResponse.from(getUser(email));
    }

    @Transactional
    @CachePut(value = "users", key = "'email:' + #result.email")
    public UserResponse createUser(UserCreateRequest request) {

        User user = new User(
            request.userName(),
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

        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new SamePasswordException();
        }
        
        user.changePassword(passwordEncoder.encode(rawPassword));
    }

    @Transactional
    @CacheEvict(value = "users", key = "'email:' + #email")
    public UserResponse updateData(String email, UserUpdateRequest updateRequest) {

        User user = getUser(email);

        log.debug("start updating user, email={}", email);

        user.updateData(
            updateRequest.userName(),
            updateRequest.phone(),
            updateRequest.address()
        );

        log.debug("finish updating user, email={}, success=true", email);

        return UserResponse.from(user);
    }

    private User getUser(String email) {
        
        log.debug("start searching user, email={}", email);

        User user =  userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not exists"));
            
        log.debug("finish searching user, email={}, found={}", 
                  email, user != null);
        return user;
    }
}
