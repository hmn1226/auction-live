package com.auctionmachine.resources.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auctionmachine.resources.model.UserModel;
import com.auctionmachine.resources.model.response.ExceptionResponse;
import com.auctionmachine.resources.repository.UserRepository;
import com.auctionmachine.util.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<?> login(String email, String rawPassword) {
        UserModel userModel = userRepository.findByEmail(email);
        
        if (userModel == null || !passwordEncoder.matches(rawPassword, userModel.getPassword())) {
            return loginError();
        }else {
            return loginSuccess(userModel);	
        }
    }

    private ResponseEntity<?> loginSuccess(UserModel userModel) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(userModel.getEmail())
                .password("")
                .roles(userModel.getRole())
                .build();

        String token = jwtUtil.generateToken(userDetails);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> loginError() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse("メールアドレスまたはパスワードが間違っています"));
    }
}