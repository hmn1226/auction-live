package com.auctionmachine.resources.schema.auction;

import java.util.List;

import com.auctionmachine.resources.model.AuctionLaneModel;
import com.auctionmachine.resources.model.AuctionRoomModel;
import com.auctionmachine.resources.model.EntryModel;
import com.auctionmachine.resources.model.EntryModel.EntryImageModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) 
public class AuctionResponse extends AuctionSchema{
	public void setAuctionRoomModel(AuctionRoomModel model) {
		super.setLiverUlid(model.getLiverUlid());
		super.setStartDatetime(model.getStartDatetime());
		super.setEndDatetime(model.getEndDatetime());
		super.setAuctionType(model.getAuctionType());
		super.setPublicMode(model.getPublicMode());
		super.setAutoPilotMode(model.getAutoPilotMode());
		super.setLaneAmount(model.getLaneAmount());
		super.setPriceInterval(model.getPriceInterval());
	}
	public void addAuctionLaneModels(List<AuctionLaneModel> models) {
		for(AuctionLaneModel model : models) {
			this.addAuctionLaneModel(model);
		}
	}
	public void addAuctionLaneModel(AuctionLaneModel model) {
		Lane lane = new Lane();
		lane.setAuctionRoomId(model.getAuctionRoomId());
		lane.setAuctionLaneId(model.getAuctionLaneId());
		//TODO
		super.getLanes().add(lane);
	}
	public void addFullEntryModels(List<EntryModel> entryModels) {
		for(EntryModel entryModel : entryModels) {
			this.addFullEntryModel(entryModel);
		}
	}
	public void addFullEntryModel(EntryModel model) {
		FullEntry fullEntry = new FullEntry();
		fullEntry.setAuctionRoomId(model.getAuctionRoomId());
		fullEntry.setAuctionLaneId(model.getAuctionLaneId());
		fullEntry.setEntryId(model.getEntryId());
		fullEntry.setEntryName(model.getEntryName());
		fullEntry.setEntryDescription(model.getEntryDescription());
		fullEntry.setStartPrice(model.getStartPrice());
		fullEntry.setQuantity(model.getQuantity());
		fullEntry.setBulkSaleMode(model.getBulkSaleMode());
		fullEntry.setCurrentPrice(model.getCurrentPrice());
		for(EntryImageModel entryImageModel : model.getEntryImages()) {
			Image image = new Image();
			image.setPath(entryImageModel.getPath());
			image.setData(entryImageModel.getData());
			fullEntry.getImages().add(image);
		}
		super.getFullEntries().add(fullEntry);
	}
	public void addPublicEntryModels(List<EntryModel> entryModels) {
		for(EntryModel entryModel : entryModels) {
			this.addPublicEntryModel(entryModel);
		}
	}
	public void addPublicEntryModel(EntryModel model) {
		PublicEntry publicEntry = new PublicEntry();
		publicEntry.setAuctionRoomId(model.getAuctionRoomId());
		publicEntry.setAuctionLaneId(model.getAuctionLaneId());
		publicEntry.setEntryId(model.getEntryId());
		publicEntry.setEntryName(model.getEntryName());
		publicEntry.setEntryDescription(model.getEntryDescription());
		publicEntry.setStartPrice(model.getStartPrice());
		publicEntry.setQuantity(model.getQuantity());
		publicEntry.setBulkSaleMode(model.getBulkSaleMode());
		publicEntry.setCurrentPrice(model.getCurrentPrice());
		for(EntryImageModel entryImageModel : model.getEntryImages()) {
			Image image = new Image();
			image.setPath(entryImageModel.getPath());
			image.setData(entryImageModel.getData());
			publicEntry.getImages().add(image);
		}
		super.getPublicEntries().add(publicEntry);
	}
}
