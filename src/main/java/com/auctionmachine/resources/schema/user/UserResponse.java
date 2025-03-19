package com.auctionmachine.resources.schema.user;

import com.auctionmachine.resources.model.UserModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserResponse extends UserSchema{
	String email;
	String password;
	String nickname;
	String ulid;
	String role;
	Boolean verified;
	
	public void set(UserModel userModel) {
		this.email = userModel.getEmail();
		this.password = userModel.getPassword();
		this.nickname = userModel.getNickname();
		this.ulid = userModel.getUlid();
		this.role = userModel.getRole();
		this.verified = userModel.getVerified();
	}
}
