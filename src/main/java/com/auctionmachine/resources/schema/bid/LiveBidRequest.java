package com.auctionmachine.resources.schema.bid;

import com.auctionmachine.util.BeanUtil;

import lombok.Data;

@Data
public class LiveBidRequest{
	private String auctionRoomId;
	private Integer auctionLaneId;
	private String entryId;
    private String bidUserId;
    private Integer bidPrice;

    @Override
    public String toString() {
        return BeanUtil.describe(this);
    }
}