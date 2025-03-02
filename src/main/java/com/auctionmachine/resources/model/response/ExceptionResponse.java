package com.auctionmachine.resources.model.response;

import lombok.Data;

@Data
public class ExceptionResponse {
	String message;
	public ExceptionResponse(String message) {
		this.message = message;
	}
}
