package com.auctionmachine.resources.schema.user;

import lombok.Data;

@Data
public class UserSchema {
	String email;
	String password;
	String nickname;
	String ulid;
	String role;
	Boolean verified;
}
