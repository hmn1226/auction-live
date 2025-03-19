package com.auctionmachine.resources.schema.bid;


import com.auctionmachine.util.BeanUtil;

import lombok.Data;

@Data
public class PreBidRequest{
	private String auctionRoomId;
	private Integer auctionLaneId;
	private String auctionEntryId;
    private int preBidPrice;
    private String userId;

    @Override
    public String toString() {
        return BeanUtil.describe(this);
    }
}