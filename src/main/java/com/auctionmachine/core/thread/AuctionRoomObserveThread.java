package com.auctionmachine.core.thread;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.auctionmachine.resources.model.AuctionLaneModel;
import com.auctionmachine.resources.model.AuctionRoomModel;
import com.auctionmachine.resources.repository.AuctionLaneRepository;
import com.auctionmachine.resources.schema.auctionlane.AuctionLaneMonitorResponse;
import com.auctionmachine.web.socket.model.MessagingModel;
import com.auctionmachine.web.socket.service.MessagingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.EqualsAndHashCode;
/*
@Component
@Data
@EqualsAndHashCode(callSuper=false)
public class AuctionRoomObserveThread extends Thread{
	
	private Logger logger = LoggerFactory.getLogger(super.getClass());
	
	private InfiniteLoopObserveThread infiniteLoopThread;
	private AuctionRoomModel auctionRoomModel;
	private AuctionLaneRepository auctionLaneRepository;
	
	public AuctionRoomObserveThread(InfiniteLoopObserveThread ilt,AuctionRoomModel arm) {
		this.infiniteLoopThread = ilt;
		this.auctionRoomModel = arm;
		this.auctionLaneRepository = ilt.getAuctionLaneRepository();
	}
	
	@Override
	public void run() {
		try {
			for(AuctionLaneModel model : this.auctionLaneRepository.get(this.auctionRoomModel.getAuctionRoomId())) {
				new AuctionLaneObserveThread(this,model).start();
			}			
		}catch(Exception e) {
			logger.error(null,e);
		}
	}
	
	
	private void send() throws JsonProcessingException {
		MessagingService ms = MessagingService.getInstance();
		if(ms!=null) {
			
			List<AuctionLaneMonitorResponse> response = new ArrayList<>();
			for(AuctionLaneModel model : this.auctionLaneRepository.get(this.auctionRoomModel.getAuctionRoomId()) ) {
				
				AuctionLaneMonitorResponse res = new AuctionLaneMonitorResponse();
				
				response.add(res);
			}
			MessagingModel message = new MessagingModel();
			ObjectMapper objectMapper = new ObjectMapper();
	        String jsonString = objectMapper.writeValueAsString(response);
	        message.setContent(jsonString);
			ms.sendMessage("/topic/messages/room1" ,message);			
		}
	}
}
*/