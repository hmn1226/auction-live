package com.auctionmachine.resources.model;

import lombok.Data;

@Data
public class VerificationModel {
	String verificationToken;
	String verificationCode;
	String email;
	String createAt;
	String expiresAt;
	boolean verified;
}
