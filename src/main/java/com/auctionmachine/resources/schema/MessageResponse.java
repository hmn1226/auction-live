package com.auctionmachine.resources.schema;

import lombok.Data;

@Data
public class MessageResponse {
	String message;
	public MessageResponse(String message) {
		this.message = message;
	}
}
