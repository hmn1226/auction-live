package com.auctionmachine.resources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auctionmachine.resources.model.UserModel;
import com.auctionmachine.resources.model.request.UserRequest;
import com.auctionmachine.resources.model.response.UserResponse;
import com.auctionmachine.resources.model.response.UsersResponse;
import com.auctionmachine.resources.model.response.UsersResponse.UserBean;
import com.auctionmachine.resources.repository.UserRepository;

import de.huxhorn.sulky.ulid.ULID;

@Service
public class UserService {
		
	@Autowired
	UserRepository userRepository;
	
	public ResponseEntity<?> get(String email){
		/*
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // 
        */
		
		if(email==null) {
			UsersResponse usersResponse = new UsersResponse();
			for(UserModel userModel : this.userRepository.findAll()) {
				UserBean userBean = usersResponse.new UserBean();
				userBean.setEmail(userModel.getEmail());
				userBean.setNickname(userModel.getNickname());
				userBean.setPassword(userModel.getPassword());
				userBean.setUlid(userModel.getUlid());
				usersResponse.getUsers().add(userBean);
			}
			return ResponseEntity.ok(usersResponse);
		}else {
			UserResponse userResponse = new UserResponse();
			UserModel userModel = this.userRepository.findByEmail(email);
			userResponse.setEmail(userModel.getEmail());
			userResponse.setNickname(userModel.getNickname());
			userResponse.setPassword(userModel.getPassword());
			userResponse.setUlid(userModel.getUlid());
			return ResponseEntity.ok(userResponse);
		}
	}
	public UserResponse put(UserRequest userRequest) {
		
		UserModel userModel = this.userRepository.findByEmail(userRequest.getEmail());
		if(userModel==null) {	
		    ULID ulid = new ULID();
		    String generatedUlid = ulid.nextULID();
		    userModel = new UserModel();
		    userModel.setEmail(userRequest.getEmail());
		    userModel.setUlid(generatedUlid);   		    
		}
	    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	    String hashedPassword = passwordEncoder.encode(userRequest.getPassword());
	    userModel.setPassword(hashedPassword);
	    userModel.setNickname(userRequest.getNickname());
	    this.userRepository.save(userModel);
		
		UserResponse response = new UserResponse();
		response.set(userModel);
		return response;
	}
	public void delete(String email) {
		this.userRepository.deleteByEmail(email);
	}
}
