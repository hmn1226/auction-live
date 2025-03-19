package com.auctionmachine.core.thread;

/*
@Component
@Data
@EqualsAndHashCode(callSuper=false)
public class AuctionLaneObserveThread extends Thread{
	
	private Logger logger = LoggerFactory.getLogger(super.getClass());
	private AuctionRoomObserveThread auctionRoomThread;
	private AuctionLaneModel auctionLaneModel;
	private AuctionLaneRepository auctionLaneRepository;
	private EntryRepository entryRepository;
	private AuctionLaneService auctionLaneService;
	
	public AuctionLaneObserveThread(AuctionRoomObserveThread art,AuctionLaneModel alm) {
		this.auctionRoomThread = art;
		this.auctionLaneModel = alm;
		this.auctionLaneRepository = art.getAuctionLaneRepository();
		this.entryRepository = art.getInfiniteLoopThread().getEntryRepository();
		this.auctionLaneService = art.getInfiniteLoopThread().getAuctionLaneService();
	}
	
	@Override
	public void run() {
		try {
			//レーンに溜まっている応札を、現在価格に反映させて解決する
			String auctionRoomId = this.auctionLaneModel.getAuctionRoomId();
			Integer auctionLaneId = this.auctionLaneModel.getAuctionLaneId();
			this.auctionLaneService.resolvePreBids(auctionRoomId,auctionLaneId);
			this.auctionLaneService.resolveLiveBids(auctionRoomId,auctionLaneId);
			
			//各エントリーの監視
			for(String entryId : this.auctionLaneModel.getEntryIds()) {
				EntryModel entryModel = this.entryRepository.getById(entryId);
			}
		}catch(Exception e) {
			logger.error(null,e);
		}
	}
}
*/