package com.auctionmachine.core.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auctionmachine.resources.model.AuctionRoomModel;
import com.auctionmachine.resources.repository.AuctionLaneRepository;
import com.auctionmachine.resources.repository.AuctionRoomRepository;
import com.auctionmachine.resources.repository.EntryRepository;
import com.auctionmachine.resources.service.AuctionLaneService;
import com.auctionmachine.resources.service.AuctionRoomService;
import com.auctionmachine.resources.service.EntryService;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Component
@Data
@EqualsAndHashCode(callSuper=false)
public class InfiniteLoopObserveThread extends Thread{
		
	private Logger logger = LoggerFactory.getLogger(super.getClass());
	
	@Autowired
	private AuctionRoomRepository auctionRoomRepository;
	@Autowired
	private AuctionLaneRepository auctionLaneRepository;
	@Autowired
	private EntryRepository entryRepository;
	@Autowired
	private AuctionRoomService auctionRoomService;
	@Autowired
	private AuctionLaneService auctionLaneService;
	@Autowired
	private EntryService entryService;
	
	@Override
	public void run() {
		try {
			while(true) {
				for(AuctionRoomModel model : auctionRoomRepository.get() ) {
					//logger.info("room"+(auctionLaneRepository==null)+" id--->"+model.getAuctionRoomId());
					//new AuctionRoomObserveThread(this,model).start();
					/*
					ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
			        int threadCount = threadMXBean.getThreadCount();
			        int daemonThreadCount = threadMXBean.getDaemonThreadCount();
			        int peakThreadCount = threadMXBean.getPeakThreadCount();
			        long totalStartedThreadCount = threadMXBean.getTotalStartedThreadCount();

			        System.out.println("現在のスレッド数: " + threadCount);
			        System.out.println("デーモンスレッド数: " + daemonThreadCount);
			        System.out.println("ピークスレッド数: " + peakThreadCount);
			        System.out.println("合計開始スレッド数: " + totalStartedThreadCount);
			        */
				}
				super.sleep(1000);
			}
		}catch(Exception e) {
			logger.error(null,e);
		}
	}
}
