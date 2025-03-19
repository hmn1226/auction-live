package com.auctionmachine.resources.schema.user;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UsersResponse{
	List<UserSchema> users = new ArrayList<>();
}
