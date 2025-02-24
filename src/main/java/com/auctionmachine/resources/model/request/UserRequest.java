package com.auctionmachine.resources.model.request;

import lombok.Data;

@Data
public class UserRequest {
	String email;
	String password;
	String nickname;
}
