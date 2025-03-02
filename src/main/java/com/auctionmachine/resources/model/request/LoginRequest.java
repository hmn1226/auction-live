package com.auctionmachine.resources.model.request;

import lombok.Data;

@Data
public class LoginRequest {
    String email;
    String password;
}
