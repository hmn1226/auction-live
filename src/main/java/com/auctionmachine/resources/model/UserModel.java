package com.auctionmachine.resources.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) 
@EqualsAndHashCode(callSuper=false)
public class UserModel extends BaseModel{
	String ulid;
	String email;
	String password;
	String nickname;
	String role;
	Boolean verified=false;
	
	public UserModel() {

	}
	
	@Override
	public String getKey() {
		return this.ulid;
	}
}
