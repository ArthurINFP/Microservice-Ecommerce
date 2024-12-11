package com.quangduy.userservice.service;


import com.quangduy.userservice.dto.LoginRequest;
import com.quangduy.userservice.dto.SignupRequest;
import com.quangduy.userservice.dto.UpdateRequest;
import com.quangduy.userservice.dto.UserProfileResponse;
import com.quangduy.userservice.entity.Role;
import com.quangduy.userservice.entity.User;
import com.quangduy.userservice.repository.UserRepository;
import com.quangduy.userservice.security.JwtTokenProvider;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public void registerUser(SignupRequest signUpRequest, boolean isVendor) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("EXISTED_EMAIL");
        }
        if (userRepository.existsByPhone(signUpRequest.getPhone())){
            throw new IllegalArgumentException("EXISTED_PHONE");
        }

        User user = new User();
        user.setFullName(signUpRequest.getFullName());
        user.setEmail(signUpRequest.getEmail());
        user.setAddress(signUpRequest.getAddress());
        user.setImageUrl(signUpRequest.getImageUrl());
        user.setPhone(signUpRequest.getPhone());
        user.setPassword(passwordEncoder.encode(
                signUpRequest.getPassword()));
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
    }

    @Transactional
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(
                "USER_NOT_FOUND"));
        return new UserProfileResponse(user);
    }
    @Transactional
    public String authenticateUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("INVALID_EMAIL"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("INVALID_PASSWORD");
        }
        String token = tokenProvider.generateToken(user);
        System.out.println("Generated JWT Token: " + token);
        return token;
    }

    @Transactional
    public UserProfileResponse getCurrentUser(Long id) {
        return new UserProfileResponse( userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "USER_NOT_FOUND")));
    }

    @Transactional
    public UserProfileResponse getCurrentUserByEmail(String email) {
        return new UserProfileResponse( userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "USER_NOT_FOUND")));
    }



    @Transactional
    public UserProfileResponse updateUserProfile(Long id,
                                  UpdateRequest updateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "USER_NOT_FOUND"));

        if (!user.getEmail().equals(updateRequest.getEmail()) &&
                userRepository.existsByEmail(updateRequest.getEmail())) {
            throw new IllegalArgumentException("EXISTED_EMAIL");
        }
        if (!user.getPhone().equals(updateRequest.getPhone()) &&
                userRepository.existsByPhone(updateRequest.getPhone())) {
            throw new IllegalArgumentException("EXISTED_PHONE");
        }
        user.setFullName(updateRequest.getFullName());
        user.setAddress(updateRequest.getAddress());
        user.setPhone(updateRequest.getPhone());
        user.setEmail(updateRequest.getEmail());
        user.setImageUrl(updateRequest.getImageUrl());
        return new UserProfileResponse(userRepository.save(user));
    }


}

