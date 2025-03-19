package com.auctionmachine.resources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auctionmachine.resources.schema.auth.AuthLoginRequest;
import com.auctionmachine.resources.schema.auth.AuthRegisterRequest;
import com.auctionmachine.resources.schema.auth.AuthVerifyRequest;
import com.auctionmachine.resources.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRegisterRequest request){
    	return authService.register(request);
    }
    
    @PostMapping("/verify")
    public ResponseEntity<?> vefify(@RequestBody AuthVerifyRequest request){
    	return authService.verify(request);
    }
}