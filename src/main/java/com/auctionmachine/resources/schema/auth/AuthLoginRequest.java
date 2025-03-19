package com.auctionmachine.resources.schema.auth;

import lombok.Data;

@Data
public class AuthLoginRequest {
    String email;
    String password;
}
