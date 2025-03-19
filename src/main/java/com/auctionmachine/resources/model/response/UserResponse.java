package com.auctionmachine.resources.model.response;

import com.auctionmachine.resources.model.UserModel;

import lombok.Data;

@Data
public class UserResponse {
	String email;
	String password;
	String nickname;
	String ulid;
	
	public void set(UserModel userModel) {
		this.email = userModel.getEmail();
		this.password = userModel.getPassword();
		this.nickname = userModel.getNickname();
		this.ulid = userModel.getUlid();
	}
}
