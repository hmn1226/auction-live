package com.auctionmachine.resources.service;

import java.time.Instant;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.auctionmachine.resources.model.AuctionLaneModel;
import com.auctionmachine.resources.model.EntryModel;
import com.auctionmachine.resources.model.LiveBidModel;
import com.auctionmachine.resources.model.PreBidModel;
import com.auctionmachine.resources.repository.AuctionLaneRepository;
import com.auctionmachine.resources.repository.EntryRepository;
import com.auctionmachine.resources.schema.bid.LiveBidRequest;
import com.auctionmachine.resources.schema.bid.PreBidRequest;

/**
 * 入札処理を行うサービスクラス
 * 事前入札とライブ入札の処理ロジックを提供する
 */
@Service
public class BidService {
    
    private final AuctionLaneRepository auctionLaneRepository;
    private final EntryRepository entryRepository;
    
    /**
     * コンストラクタインジェクション
     * 
     * @param auctionLaneRepository オークションレーンリポジトリ
     * @param entryRepository エントリーリポジトリ
     */
    public BidService(AuctionLaneRepository auctionLaneRepository, EntryRepository entryRepository) {
        this.auctionLaneRepository = auctionLaneRepository;
        this.entryRepository = entryRepository;
    }
    
    /**
     * 事前入札を処理する
     * 
     * @param req 事前入札リクエスト
     * @throws Exception リポジトリアクセス時の例外
     */
    public void preBid(PreBidRequest req) throws Exception {
        AuctionLaneModel auctionLaneModel = 
                this.auctionLaneRepository.getById(req.getAuctionRoomId(), req.getAuctionLaneId());
        
        EntryModel entryModel = this.entryRepository.getById(auctionLaneModel.getCurrentEntryId());

        PreBidModel preBidModel = new PreBidModel();
        preBidModel.setBidUserId(req.getUserId());
        preBidModel.setAuctionEntryId(req.getAuctionEntryId());
        preBidModel.setBidTime(Instant.now());
        preBidModel.setBidPrice(req.getPreBidPrice());
        entryModel.getPreBidList().add(preBidModel);
    }
    
    /**
     * ライブ入札を処理する
     * 
     * @param req ライブ入札リクエスト
     * @throws Exception リポジトリアクセス時の例外
     */
    public void liveBid(LiveBidRequest req) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ulid = authentication.getName();
        
        AuctionLaneModel auctionLaneModel = 
                this.auctionLaneRepository.getById(req.getAuctionRoomId(), req.getAuctionLaneId());
        
        EntryModel entryModel = this.entryRepository.getById(auctionLaneModel.getCurrentEntryId());
        
        if (req.getBidUserId() == null) {
            req.setBidUserId(ulid);            
        }
        
        LiveBidModel liveBidModel = new LiveBidModel();
        liveBidModel.setBidUserId(req.getBidUserId());
        liveBidModel.setAuctionEntryId(req.getEntryId());
        liveBidModel.setBidTime(Instant.now());
        
        auctionLaneModel.getLiveBidQueue().add(liveBidModel);
        entryModel.getLiveBidList().add(liveBidModel);
        
        // リポジトリに変更を保存
        this.auctionLaneRepository.put(auctionLaneModel);
        this.entryRepository.put(entryModel);
    }
}
