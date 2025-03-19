package com.auctionmachine.resources.schema.auth;

import lombok.Data;

@Data
public class AuthRegisterRequest {
	String email;
	String nickname;
	String password;
}
