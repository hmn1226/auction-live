package com.auctionmachine.resources.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auctionmachine.resources.model.request.AuctionRequest;
import com.auctionmachine.resources.model.response.AuctionResponse;
import com.auctionmachine.resources.model.response.AuctionsResponse;
import com.auctionmachine.resources.service.AuctionService;

@RestController
public class AuctionController {
	
	@Autowired
	AuctionService auctionService;
	
	@GetMapping("/api/auctions")
	public AuctionsResponse get() {
		return this.auctionService.get();
	}
	
	@PutMapping("/api/auctions/{auctionRoomId}")
	public AuctionResponse put(
		@PathVariable("auctionRoomId") String auctionRoomId,
        @RequestBody AuctionRequest auctionRequest
	) {
		auctionRequest.setAuctionRoomId(auctionRoomId);
		return this.auctionService.put(auctionRequest);
	}
	
	@DeleteMapping("/api/auctions/{auctionRoomId}")
	public void delete(
		@PathVariable("auctionRoomId") String auctionRoomId
	) {
		this.auctionService.delete(auctionRoomId);
	}
}
