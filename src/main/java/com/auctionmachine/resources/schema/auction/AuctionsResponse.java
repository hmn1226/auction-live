package com.auctionmachine.resources.schema.auction;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AuctionsResponse {
	
	List<AuctionSchema> auctions = new ArrayList<>();
}
