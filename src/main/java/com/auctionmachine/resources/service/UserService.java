package com.auctionmachine.resources.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auctionmachine.resources.model.UserModel;
import com.auctionmachine.resources.repository.UserRepository;
import com.auctionmachine.resources.schema.user.UserRequest;
import com.auctionmachine.resources.schema.user.UserResponse;
import com.auctionmachine.resources.schema.user.UserSchema;
import com.auctionmachine.resources.schema.user.UsersResponse;

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
			for(UserModel userModel : this.userRepository.getAll()) {
				UserSchema userSchema = new UserSchema();
				userSchema.setEmail(userModel.getEmail());
				userSchema.setNickname(userModel.getNickname());
				userSchema.setPassword(userModel.getPassword());
				userSchema.setUlid(userModel.getUlid());
				userSchema.setRole(userModel.getRole());
				userSchema.setVerified(userModel.getVerified());
				usersResponse.getUsers().add(userSchema);
			}
			return ResponseEntity.ok(usersResponse);
		}else {
			UserResponse userResponse = new UserResponse();
			UserModel userModel = this.userRepository.getByEmail(email);
			userResponse.setEmail(userModel.getEmail());
			userResponse.setNickname(userModel.getNickname());
			userResponse.setPassword(userModel.getPassword());
			userResponse.setUlid(userModel.getUlid());
			userResponse.setRole(userModel.getRole());
			userResponse.setVerified(userModel.getVerified());
			return ResponseEntity.ok(userResponse);
		}
	}
	public UserResponse put(UserRequest userRequest) {
		
		UserModel userModel = this.userRepository.getByEmail(userRequest.getEmail());
		if(userModel==null) {//---新規登録
		    ULID ulid = new ULID();
		    String generatedUlid = ulid.nextULID();
		    userModel = new UserModel();
		    userModel.setEmail(userRequest.getEmail());
		    userModel.setUlid(generatedUlid);   
		    userModel.setNickname(userRequest.getNickname());
		    userModel.setRole(userRequest.getRole());
		    userModel.setVerified(userRequest.getVerified());
		}else {//---更新の場合
		    userModel.setNickname(userRequest.getNickname());
		    userModel.setRole(userRequest.getRole());		
		    userModel.setVerified(userRequest.getVerified());
		}
		if(userRequest.getPassword()!=null) {
		    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		    String hashedPassword = passwordEncoder.encode(userRequest.getPassword());
		    userModel.setPassword(hashedPassword);
		}
		
	    this.userRepository.save(userModel);
		UserResponse response = new UserResponse();
		response.set(userModel);
		return response;
	}
	public void deleteByEmail(String email) {
		this.userRepository.deleteByEmail(email);
	}
}
