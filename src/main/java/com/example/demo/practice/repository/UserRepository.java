package com.example.demo.practice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.practice.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}