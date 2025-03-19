package com.auctionmachine.resources.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.auctionmachine.resources.model.AuctionLaneModel;
import com.auctionmachine.resources.model.AuctionRoomModel;
import com.auctionmachine.resources.model.EntryModel;
import com.auctionmachine.resources.repository.AuctionLaneRepository;
import com.auctionmachine.resources.repository.AuctionRoomRepository;
import com.auctionmachine.resources.repository.EntryRepository;
import com.auctionmachine.resources.schema.auction.AuctionRequest;
import com.auctionmachine.resources.schema.auction.AuctionResponse;
import com.auctionmachine.resources.schema.auction.AuctionSchema;
import com.auctionmachine.resources.schema.auction.AuctionsResponse;
import com.auctionmachine.util.BeanUtil;

@Service
public class AuctionRoomService {
	
	@Autowired
	AuctionRoomRepository auctionRoomRepository;
	
	@Autowired
	AuctionLaneRepository auctionLaneRepository;
	
	@Autowired
	EntryRepository entryRepository;
		
	public AuctionsResponse get() throws Exception{
		AuctionsResponse ret = new AuctionsResponse();
		for(AuctionRoomModel model : this.auctionRoomRepository.get()) {
			AuctionSchema bean = new AuctionSchema();
			bean.setAuctionRoomId(model.getAuctionRoomId());
			bean.setLiverUlid(model.getLiverUlid());
			ret.getAuctions().add(bean);
		}
		return ret;
	}
	
	private enum EntryModelType {FULL, PUBLIC}
	public AuctionResponse getFullById(String auctionRoomId) throws Exception{
		return this._getById(auctionRoomId, EntryModelType.FULL);
	}
	public AuctionResponse getPublicById(String auctionRoomId) throws Exception{
		return this._getById(auctionRoomId, EntryModelType.PUBLIC);
	}
	public AuctionResponse _getById(String auctionRoomId,EntryModelType entryModelType) throws Exception{
		AuctionResponse auctionResponse = new AuctionResponse();
		
		AuctionRoomModel auctionRoomModel = this.auctionRoomRepository.getById(auctionRoomId);
		auctionResponse.setAuctionRoomModel(auctionRoomModel);
		
		List<AuctionLaneModel> auctionLaneModels = this.auctionLaneRepository.get(auctionRoomId);
		auctionResponse.addAuctionLaneModels(auctionLaneModels);
		
		for(AuctionLaneModel auctionLaneModel : auctionLaneModels) {
			for(String entryId : auctionLaneModel.getEntryIds()) {
				switch(entryModelType) {
				case FULL:
					auctionResponse.addFullEntryModel(this.entryRepository.getById(entryId));
					break;
				case PUBLIC:
					auctionResponse.addPublicEntryModel(this.entryRepository.getById(entryId));
					break;
				}
								
			}
		}
	    return auctionResponse;
	}
	public AuctionResponse put(AuctionRequest auctionRequest) throws Exception{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ulid = authentication.getName();
        auctionRequest.setLiverUlid(ulid);
        
		AuctionRoomModel auctionModel = auctionRequest.getAuctionRoomModel();
		List<AuctionLaneModel> auctionLaneModels = auctionRequest.getAuctionLaneModel();
		List<EntryModel> entryModels = auctionRequest.getEntryModel();
		
		this.auctionRoomRepository.put(auctionModel);
		this.auctionLaneRepository.put(auctionLaneModels);
		this.entryRepository.put(entryModels);
		
		AuctionResponse auctionResponse = BeanUtil.deepCopy(AuctionResponse.class,auctionRequest);
		return auctionResponse;
	}
	public void delete(String auctionRoomId) throws Exception{
		this.auctionRoomRepository.delete(auctionRoomId);
	}
	public Map<String,String> generate(){
		Map<String,String> ret = new HashMap<>();
		ret.put("auctionRoomId",generateRandomId());
		return ret;
	}
	private static String generateRandomId() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            char letter = (char) ('a' + random.nextInt(26));
            sb.append(letter);
        }
        sb.append("-");
        for (int i = 0; i < 4; i++) {
            char letter = (char) ('a' + random.nextInt(26));
            sb.append(letter);
        }
        sb.append("-");
        for (int i = 0; i < 3; i++) {
            char letter = (char) ('a' + random.nextInt(26));
            sb.append(letter);
        }
        return sb.toString();
    }
}