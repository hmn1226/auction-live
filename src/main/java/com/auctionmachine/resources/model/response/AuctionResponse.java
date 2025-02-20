package com.auctionmachine.resources.model.response;

import com.auctionmachine.resources.model.request.AuctionRequest;

import lombok.Data;

@Data
public class AuctionResponse {
	String auctionRoomId;
	String liverUlid;
	
	public AuctionResponse() {
		
	}
	public AuctionResponse(AuctionRequest auctionRequest) {
		this.auctionRoomId = auctionRequest.getAuctionRoomId();
		this.liverUlid = auctionRequest.getLiverUlid();
	}
}
