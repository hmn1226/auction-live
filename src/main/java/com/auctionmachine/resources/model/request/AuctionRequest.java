package com.auctionmachine.resources.model.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AuctionRequest{
	String auctionRoomId;
	String liverUlid;
	List<Entry> entries = new ArrayList<>();

	@Data
	public static class Entry{
		String entryId; 
	}
}
