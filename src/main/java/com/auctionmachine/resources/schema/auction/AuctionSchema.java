package com.auctionmachine.resources.schema.auction;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AuctionSchema {
	String auctionRoomId;
	String liverUlid;
	
	String startDatetime;
	String endDatetime;
	Integer auctionType;
	Integer autoPilotMode;
	Integer publicMode;
	Integer laneAmount;
	Integer priceInterval;
	
	List<Lane> lanes = new ArrayList<>();
	List<FullEntry> fullEntries = new ArrayList<>();
	List<PublicEntry> publicEntries = new ArrayList<>();
	
	@Data
	public static class Lane{
		String auctionRoomId;
		Integer auctionLaneId;
	}
	
	@Data
	public static class FullEntry{
		String auctionRoomId;
		Integer auctionLaneId;
		String entryId; 
		String entryName;
		Integer startPrice;
		Integer slowPrice;
		Integer reservePrice;
		Integer quantity;
		Integer bulkSaleMode;
		String entryDescription;
		Integer currentPrice;
		List<Image> images = new ArrayList<>();
	}
	@Data
	public static class PublicEntry{
		String auctionRoomId;
		Integer auctionLaneId;
		String entryId; 
		String entryName;
		Integer startPrice;
		Integer quantity;
		Integer bulkSaleMode;
		String entryDescription;
		Integer currentPrice;
		List<Image> images = new ArrayList<>();
	}
	@Data
	public static class Image{
		String path;
		String relativePath;
		String data;
	}
}
