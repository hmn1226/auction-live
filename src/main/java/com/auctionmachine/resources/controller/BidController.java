package com.auctionmachine.resources.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
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
     * @return 処理結果
     * @throws Exception 処理中の例外
     */
    @PostMapping("/api/pre-bid/{auctionRoomId}/{auctionLaneId}/{entryId}")
    public ResponseEntity<Map<String, Object>> preBid(
            @PathVariable String auctionRoomId,
            @PathVariable Integer auctionLaneId,
            @PathVariable String entryId,
            @RequestBody PreBidRequest preBidRequest
    ) throws Exception {
        preBidRequest.setAuctionRoomId(auctionRoomId);
        preBidRequest.setAuctionLaneId(auctionLaneId);
        preBidRequest.setAuctionEntryId(entryId);
        this.bidService.preBid(preBidRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "事前入札が受け付けられました");
        return ResponseEntity.ok(response);
    }
    
    /**
     * ライブ入札を処理する
     * 
     * @param auctionRoomId オークションルームID
     * @param auctionLaneId オークションレーンID
     * @param entryId エントリーID
     * @param liveBidRequest ライブ入札リクエスト
     * @return 処理結果
     * @throws Exception 処理中の例外
     */
    @PostMapping("/api/live-bid/{auctionRoomId}/{auctionLaneId}/{entryId}")
    public ResponseEntity<Map<String, Object>> liveBid(
            @PathVariable String auctionRoomId,
            @PathVariable Integer auctionLaneId,
            @PathVariable String entryId,
            @RequestBody LiveBidRequest liveBidRequest
    ) throws Exception {
        // リクエスト情報をログ出力
        System.out.println("ライブ入札リクエスト受信: " + auctionRoomId + "/" + auctionLaneId + "/" + entryId);
        System.out.println("リクエストボディ: " + liveBidRequest);
        
        liveBidRequest.setAuctionRoomId(auctionRoomId);
        liveBidRequest.setAuctionLaneId(auctionLaneId);
        liveBidRequest.setEntryId(entryId);
        this.bidService.liveBid(liveBidRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "ライブ入札が受け付けられました");
        return ResponseEntity.ok(response);
    }
}
