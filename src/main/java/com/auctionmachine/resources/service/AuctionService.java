package com.auctionmachine.resources.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auctionmachine.resources.model.AuctionModel;
import com.auctionmachine.resources.model.request.AuctionRequest;
import com.auctionmachine.resources.model.response.AuctionResponse;
import com.auctionmachine.resources.model.response.AuctionsResponse;
import com.auctionmachine.resources.model.response.AuctionsResponse.AuctionBean;
import com.auctionmachine.util.RedisUtil;

@Service
public class AuctionService {
	
	@Autowired
	RedisUtil redisUtil;
	
	public AuctionsResponse get() {
		AuctionsResponse ret = new AuctionsResponse();
		Set<String> keys = this.redisUtil.scanKeys("auction:");
		for(String key : keys) {
			AuctionModel model = (AuctionModel)this.redisUtil.getObject(key);
			AuctionBean bean = ret.new AuctionBean();
			bean.setAuctionRoomId(model.getAuctionRoomId());
			bean.setLiverUlid(model.getLiverUlid());
			ret.getAuctions().add(bean);
		}
		return ret;
	}
	
	public AuctionResponse put(AuctionRequest auctionRequest) {
		AuctionModel auctionModel = new AuctionModel();
		auctionModel.setAuctionRoomId(auctionRequest.getAuctionRoomId());
		auctionModel.setLiverUlid(auctionRequest.getLiverUlid());
		this.redisUtil.saveObject("auction:"+auctionRequest.getAuctionRoomId(), auctionModel);
		return new AuctionResponse(auctionRequest);
	}
	
	public void delete(String auctionRoomId) {
		this.redisUtil.deleteObject("auction:"+auctionRoomId);
	}
}