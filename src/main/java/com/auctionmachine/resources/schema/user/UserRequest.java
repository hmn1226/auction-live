package com.auctionmachine.resources.schema.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserRequest extends UserSchema{
	String email;
	String password;
	String nickname;
    String role;
    Boolean verified;
}
