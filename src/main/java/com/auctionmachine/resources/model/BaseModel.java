package com.auctionmachine.resources.model;

import com.auctionmachine.util.BeanUtil;

import lombok.Data;

@Data
public abstract class BaseModel {
	
	public abstract String getKey();
	
	public String toString() {
		return BeanUtil.describe(this);
	}
}
