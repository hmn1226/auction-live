package com.auctionmachine.resources.schema.auth;

import lombok.Data;

@Data
public class AuthVerifyRequest {
	String verificationToken;
	String verificationCode;
}
