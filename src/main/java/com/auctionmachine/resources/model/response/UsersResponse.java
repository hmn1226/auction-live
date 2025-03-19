package com.auctionmachine.resources.model.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UsersResponse {
	List<UserBean> users = new ArrayList<>();
	
	@Data
	public class UserBean{
		String email;
		String password;
		String nickname;
		String ulid;
	}
}
