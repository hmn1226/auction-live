package com.auctionmachine.resources.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.auctionmachine.util.RedisUtil;

@Repository
public class AuctionRepository {
	
	@Autowired
	AuctionRoomRepository auctionRoomRepository;
	
	@Autowired
	AuctionLaneRepository auctionLaneRepository;
	
	@Autowired
	RedisUtil redisUtil;
	
	
}
