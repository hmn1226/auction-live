package com.auctionmachine.resources.model.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AuctionsResponse {
	
	List<AuctionBean> auctions = new ArrayList<>();
	
	@Data
	public class AuctionBean{
		String auctionRoomId;
		String liverUlid;
	}
}
