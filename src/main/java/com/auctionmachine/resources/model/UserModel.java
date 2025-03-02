package com.auctionmachine.resources.model;

import lombok.Data;

@Data
public class UserModel {
	String email;
	String password;
	String nickname;
	String ulid;
	String role;
}
