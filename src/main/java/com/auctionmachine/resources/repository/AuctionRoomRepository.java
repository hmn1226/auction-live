package com.auctionmachine.resources.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.auctionmachine.resources.model.AuctionRoomModel;

@Repository
public class AuctionRoomRepository extends BaseRepository{
	public AuctionRoomRepository() {
		super("auctionRoom");
	}
	public List<AuctionRoomModel> get() {
		return super.findAll(AuctionRoomModel.class,super.prefix+":");
	}
	public AuctionRoomModel getById(String auctionRoomId) throws Exception{
		return super.findById(AuctionRoomModel.class, super.getPrefix()+":"+auctionRoomId);
	}
	public void put(AuctionRoomModel auctionModel) {
		super.save(auctionModel);
	}	
	public void delete(String auctionRoomId) {
		this.deleteById("auctionRoom:"+auctionRoomId);
	}
}
