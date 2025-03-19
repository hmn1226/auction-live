package com.auctionmachine.resources.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auctionmachine.resources.schema.bid.LiveBidRequest;
import com.auctionmachine.resources.schema.bid.PreBidRequest;
import com.auctionmachine.resources.service.BidService;

/**
 * 入札操作を処理するコントローラー
 * 事前入札とライブ入札のエンドポイントを提供する
 */
@RestController
public class BidController {

    private final BidService bidService;
    
    /**
     * コンストラクタインジェクション
     * 
     * @param bidService 入札サービス
     */
    public BidController(BidService bidService) {
        this.bidService = bidService;
    }
    
    /**
     * 事前入札を処理する
     * 
     * @param auctionRoomId オークションルームID
     * @param auctionLaneId オークションレーンID
     * @param entryId エントリーID
     * @param preBidRequest 事前入札リクエスト
     * @throws Exception 処理中の例外
     */
    @PostMapping("/api/pre-bid/{auctionRoomId}/{auctionLaneId}/{entryId}")
    public void preBid(
            @PathVariable String auctionRoomId,
            @PathVariable Integer auctionLaneId,
            @PathVariable String entryId,
            @RequestBody PreBidRequest preBidRequest
    ) throws Exception {
        preBidRequest.setAuctionRoomId(auctionRoomId);
        preBidRequest.setAuctionLaneId(auctionLaneId);
        preBidRequest.setAuctionEntryId(entryId);
        this.bidService.preBid(preBidRequest);
    }
    
    /**
     * ライブ入札を処理する
     * 
     * @param auctionRoomId オークションルームID
     * @param auctionLaneId オークションレーンID
     * @param entryId エントリーID
     * @param liveBidRequest ライブ入札リクエスト
     * @throws Exception 処理中の例外
     */
    @PostMapping("/api/live-bid/{auctionRoomId}/{auctionLaneId}/{entryId}")
    public void liveBid(
            @PathVariable String auctionRoomId,
            @PathVariable Integer auctionLaneId,
            @PathVariable String entryId,
            @RequestBody LiveBidRequest liveBidRequest
    ) throws Exception {
        liveBidRequest.setAuctionRoomId(auctionRoomId);
        liveBidRequest.setAuctionLaneId(auctionLaneId);
        liveBidRequest.setEntryId(entryId);
        this.bidService.liveBid(liveBidRequest);
    }
}
