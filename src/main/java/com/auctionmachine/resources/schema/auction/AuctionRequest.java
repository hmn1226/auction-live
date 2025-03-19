package com.auctionmachine.resources.schema.auction;

import java.util.ArrayList;
import java.util.List;

import com.auctionmachine.resources.model.AuctionLaneModel;
import com.auctionmachine.resources.model.AuctionRoomModel;
import com.auctionmachine.resources.model.EntryModel;

public class AuctionRequest extends AuctionSchema{
	
	public AuctionRoomModel getAuctionRoomModel() {
		AuctionRoomModel ret = new AuctionRoomModel();
		ret.setAuctionRoomId(super.auctionRoomId);
		ret.setLiverUlid(super.getLiverUlid());
		ret.setStartDatetime(super.getStartDatetime());
		ret.setEndDatetime(super.getEndDatetime());
		ret.setAuctionType(super.getAuctionType());
		ret.setPublicMode(super.getPublicMode());
		ret.setAutoPilotMode(super.getAutoPilotMode());
		ret.setLaneAmount(super.getLaneAmount());
		ret.setPriceInterval(super.getPriceInterval());
		return ret;
	}
	
	public List<AuctionLaneModel>  getAuctionLaneModel(){
		List<AuctionLaneModel> ret = new ArrayList<>();
		for(Lane lane : super.getLanes()) {
			AuctionLaneModel auctionLaneModel = new AuctionLaneModel();
			auctionLaneModel.setAuctionLaneId(lane.getAuctionLaneId());
			//TODO 未実装
			ret.add(auctionLaneModel);
		}
		return ret;
	}
	public List<EntryModel> getEntryModel(){
		List<EntryModel> ret = new ArrayList<>();
		for(FullEntry fullEntry : super.getFullEntries()) {
			EntryModel entryModel = new EntryModel();
			entryModel.setEntryId(fullEntry.getEntryId());
			entryModel.setEntryName(fullEntry.getEntryName());
			entryModel.setEntryDescription(fullEntry.getEntryDescription());
			entryModel.setStartPrice(fullEntry.getStartPrice());
			entryModel.setSlowPrice(fullEntry.getSlowPrice());
			entryModel.setReservePrice(fullEntry.getReservePrice());
			entryModel.setQuantity(fullEntry.getQuantity());
			entryModel.setBulkSaleMode(fullEntry.getBulkSaleMode());
			ret.add(entryModel);
		}
		return ret;
	}
}
