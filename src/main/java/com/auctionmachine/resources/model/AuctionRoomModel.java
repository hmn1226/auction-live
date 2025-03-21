package com.auctionmachine.resources.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Component
@JsonIgnoreProperties(ignoreUnknown = true) 
@EqualsAndHashCode(callSuper=false)
public class AuctionRoomModel extends BaseModel{
	String auctionRoomId;
	String liverUlid;
	String startDatetime;
	String endDatetime;
	Integer auctionType;
	Integer publicMode;
	Integer autoPilotMode;
	Integer laneAmount;
	Integer priceInterval;
	
	List<Integer> auctionLaneIds = new ArrayList<>();
	
	public AuctionRoomModel() {
		
	}	
	@Override
	public String getKey() {
		return this.auctionRoomId;
	}
}
