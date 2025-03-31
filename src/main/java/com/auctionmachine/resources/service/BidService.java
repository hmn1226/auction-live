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
import com.auctionmachine.util.RedisUtil;

/**
 * 入札処理を行うサービスクラス
 * 事前入札とライブ入札の処理ロジックを提供する
 */
@Service
public class BidService {
    
    private final AuctionLaneRepository auctionLaneRepository;
    private final EntryRepository entryRepository;
    private final RedisUtil redisUtil;
    
    // Redis Streamsのキー
    private static final String LIVE_BID_STREAM_KEY = "stream:live-bids";
    
    /**
     * コンストラクタインジェクション
     * 
     * @param auctionLaneRepository オークションレーンリポジトリ
     * @param entryRepository エントリーリポジトリ
     * @param redisUtil Redisユーティリティ
     */
    public BidService(AuctionLaneRepository auctionLaneRepository, EntryRepository entryRepository, RedisUtil redisUtil) {
        this.auctionLaneRepository = auctionLaneRepository;
        this.entryRepository = entryRepository;
        this.redisUtil = redisUtil;
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
        
        // Redis Streamsにライブ応札信号を追加（最初に行う）
        String recordId = redisUtil.addToStream(LIVE_BID_STREAM_KEY, req);
        if (recordId != null) {
            // ログ出力
            System.out.println("ライブ応札信号をRedis Streamsに追加しました: " + recordId);
        }
        
        try {
            AuctionLaneModel auctionLaneModel = 
                    this.auctionLaneRepository.getById(req.getAuctionRoomId(), req.getAuctionLaneId());
            
            // エントリーIDが設定されていない場合は、レーンの現在のエントリーIDを使用
            String entryId = req.getEntryId();
            if (entryId == null || entryId.isEmpty()) {
                entryId = auctionLaneModel.getCurrentEntryId();
                req.setEntryId(entryId);
            }
            
            // エントリーIDが有効な場合のみ処理を続行
            if (entryId != null && !entryId.isEmpty()) {
                EntryModel entryModel = this.entryRepository.getById(entryId);
                
                if (req.getBidUserId() == null) {
                    req.setBidUserId(ulid);            
                }
                
                LiveBidModel liveBidModel = new LiveBidModel();
                liveBidModel.setBidUserId(req.getBidUserId());
                liveBidModel.setAuctionEntryId(entryId);
                liveBidModel.setBidTime(Instant.now());
                
                auctionLaneModel.getLiveBidQueue().add(liveBidModel);
                entryModel.getLiveBidList().add(liveBidModel);
                
                // リポジトリに変更を保存
                this.auctionLaneRepository.put(auctionLaneModel);
                this.entryRepository.put(entryModel);
            } else {
                System.out.println("有効なエントリーIDがありません。Redis Streamsへの追加のみ行いました。");
            }
        } catch (Exception e) {
            System.out.println("エントリー処理中にエラーが発生しましたが、Redis Streamsへの追加は完了しています: " + e.getMessage());
            // エラーを再スローしない - Redis Streamsへの追加は成功しているため
        }
    }
}
