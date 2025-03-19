package com.auctionmachine.resources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auctionmachine.resources.schema.user.UserRequest;
import com.auctionmachine.resources.schema.user.UserResponse;
import com.auctionmachine.resources.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/api/users")
	public ResponseEntity<?> get(
		@RequestParam(required=false) String email
	)throws Exception {
		return this.userService.get(email);
	}
	
	@PutMapping("/api/users/{email}")
	public UserResponse put(
		@PathVariable String email,
        @RequestBody UserRequest userRequest
	)throws Exception {
		userRequest.setEmail(email);
		return this.userService.put(userRequest);
	}
	
	@DeleteMapping("/api/users/{email}")
	public void delete(
		@PathVariable String email
	)throws Exception {
		this.userService.deleteByEmail(email);
	}
	
}
