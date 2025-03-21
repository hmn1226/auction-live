package com.auctionmachine.resources.schema.auctionlane;

import jakarta.validation.constraints.NotNull;

import com.auctionmachine.util.BeanUtil;

import lombok.Data;

@Data
public class CurrentPriceRequest{
	private String auctionRoomId;
	private Integer auctionLaneId;
	@NotNull
	private Integer currentPrice;
	
    @Override
    public String toString() {
        return BeanUtil.describe(this);
    }
}