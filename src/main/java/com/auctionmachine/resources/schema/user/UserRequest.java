package com.auctionmachine.resources.schema.user;

import lombok.Data;

@Data
public class UserRequest extends UserSchema{
	String email;
	String password;
	String nickname;
    String role;
    Boolean verified;
}
