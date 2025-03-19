package com.auctionmachine.resources.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.auctionmachine.resources.model.AuctionLaneModel;

@Repository
public class AuctionLaneRepository extends BaseRepository{
	public AuctionLaneRepository() {
		super("auctionLane");
	}
	public List<AuctionLaneModel> get(String auctionRoomId) {
		return super.findAll(AuctionLaneModel.class, super.getPrefix()+":"+auctionRoomId);
	}
	public AuctionLaneModel getById(String auctionRoomId,Integer auctionLaneId) throws Exception{
		return super.findById(AuctionLaneModel.class, super.getPrefix()+":" + auctionRoomId+"-"+auctionLaneId);
	}
	public void put(AuctionLaneModel auctionLaneModel) {
		super.save(auctionLaneModel);
	}
	public void put(List<AuctionLaneModel> auctionLaneModels) {
		super.save(auctionLaneModels);
	}
	public void delete(String auctionRoomId,Integer auctionLaneId) {
		super.deleteById("auctionLane:"+auctionRoomId+"-"+auctionLaneId);
	}
}
