package com.auctionmachine.resources.schema.auctionroom;

import com.auctionmachine.util.BeanUtil;

import lombok.Data;

@Data
public class AuctionRoomRequest{
	private String auctionRoomId;
	
    @Override
    public String toString() {
        return BeanUtil.describe(this);
    }
}