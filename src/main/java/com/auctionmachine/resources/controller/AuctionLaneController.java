package com.auctionmachine.resources.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auctionmachine.resources.schema.auctionlane.AuctionLaneRequest;
import com.auctionmachine.resources.schema.auctionlane.CurrentPriceRequest;
import com.auctionmachine.resources.service.AuctionLaneService;

/**
 * オークションレーン操作を処理するコントローラー
 * レーンのステータス変更、価格更新、エントリー移動、入札解決などのエンドポイントを提供する
 */
@RestController
public class AuctionLaneController {

    private final AuctionLaneService auctionLaneService;
    
    /**
     * コンストラクタインジェクション
     * 
     * @param auctionLaneService オークションレーンサービス
     */
    public AuctionLaneController(AuctionLaneService auctionLaneService) {
        this.auctionLaneService = auctionLaneService;
    }
    
    /**
     * レーンのステータスを更新する
     * 
     * @param auctionRoomId オークションルームID
     * @param auctionLaneId オークションレーンID
     * @param auctionLaneRequest リクエスト情報
     * @throws Exception 処理中の例外
     */
    @PostMapping("/api/auction-lane/{auctionRoomId}/{auctionLaneId}/status")
    public void status(
            @PathVariable String auctionRoomId,
            @PathVariable Integer auctionLaneId,
            @RequestBody AuctionLaneRequest auctionLaneRequest
    ) throws Exception {
        auctionLaneRequest.setAuctionRoomId(auctionRoomId);
        auctionLaneRequest.setAuctionLaneId(auctionLaneId);
        this.auctionLaneService.status(auctionLaneRequest);
    }
    
    /**
     * 現在価格を更新する
     * 
     * @param auctionRoomId オークションルームID
     * @param auctionLaneId オークションレーンID
     * @param currentPriceRequest 価格更新リクエスト
     * @throws Exception 処理中の例外
     */
    @PostMapping("/api/auction-lane/{auctionRoomId}/{auctionLaneId}/current-price")
    public void currentPrice(
            @PathVariable String auctionRoomId,
            @PathVariable Integer auctionLaneId,
            @RequestBody CurrentPriceRequest currentPriceRequest
    ) throws Exception {
        currentPriceRequest.setAuctionRoomId(auctionRoomId);
        currentPriceRequest.setAuctionLaneId(auctionLaneId);
        this.auctionLaneService.currentPrice(currentPriceRequest);
    }
    
    /**
     * 次のエントリーに移動する
     * 
     * @param auctionRoomId オークションルームID
     * @param auctionLaneId オークションレーンID
     * @param auctionLaneRequest リクエスト情報
     * @throws Exception 処理中の例外
     */
    @PostMapping("/api/auction-lane/{auctionRoomId}/{auctionLaneId}/next-entry")
    public void nextEntry(
            @PathVariable String auctionRoomId,
            @PathVariable Integer auctionLaneId,
            @RequestBody AuctionLaneRequest auctionLaneRequest
    ) throws Exception {
        auctionLaneRequest.setAuctionRoomId(auctionRoomId);
        auctionLaneRequest.setAuctionLaneId(auctionLaneId);
        this.auctionLaneService.nextEntry(auctionLaneRequest);
    }
    
    /**
     * 前のエントリーに移動する
     * 
     * @param auctionRoomId オークションルームID
     * @param auctionLaneId オークションレーンID
     * @param auctionLaneRequest リクエスト情報
     * @throws Exception 処理中の例外
     */
    @PostMapping("/api/auction-lane/{auctionRoomId}/{auctionLaneId}/prev-entry")
    public void prevEntry(
            @PathVariable String auctionRoomId,
            @PathVariable Integer auctionLaneId,
            @RequestBody AuctionLaneRequest auctionLaneRequest
    ) throws Exception {
        auctionLaneRequest.setAuctionRoomId(auctionRoomId);
        auctionLaneRequest.setAuctionLaneId(auctionLaneId);
        this.auctionLaneService.prevEntry(auctionLaneRequest);
    }
    
    /**
     * すべての入札を解決する
     * 
     * @param auctionRoomId オークションルームID
     * @param auctionLaneId オークションレーンID
     * @param auctionLaneRequest リクエスト情報
     * @throws Exception 処理中の例外
     */
    @PostMapping("/api/auction-lane/{auctionRoomId}/{auctionLaneId}/resolve-bids")
    public void resolveBids(
            @PathVariable String auctionRoomId,
            @PathVariable Integer auctionLaneId,
            @RequestBody AuctionLaneRequest auctionLaneRequest
    ) throws Exception {
        auctionLaneRequest.setAuctionRoomId(auctionRoomId);
        auctionLaneRequest.setAuctionLaneId(auctionLaneId);
        this.auctionLaneService.resolvePreBids(auctionLaneRequest);
        this.auctionLaneService.resolveLiveBids(auctionLaneRequest);
    }

    /**
     * ライブ入札のみを解決する
     * 
     * @param auctionRoomId オークションルームID
     * @param auctionLaneId オークションレーンID
     * @param auctionLaneRequest リクエスト情報
     * @throws Exception 処理中の例外
     */
    @PostMapping("/api/auction-lane/{auctionRoomId}/{auctionLaneId}/resolve-live-bids")
    public void resolveLiveBids(
            @PathVariable String auctionRoomId,
            @PathVariable Integer auctionLaneId,
            @RequestBody AuctionLaneRequest auctionLaneRequest
    ) throws Exception {
        auctionLaneRequest.setAuctionRoomId(auctionRoomId);
        auctionLaneRequest.setAuctionLaneId(auctionLaneId);
        this.auctionLaneService.resolveLiveBids(auctionLaneRequest);
    }
    
    /**
     * 事前入札のみを解決する
     * 
     * @param auctionRoomId オークションルームID
     * @param auctionLaneId オークションレーンID
     * @param auctionLaneRequest リクエスト情報
     * @throws Exception 処理中の例外
     */
    @PostMapping("/api/auction-lane/{auctionRoomId}/{auctionLaneId}/resolve-pre-bids")
    public void resolvePreBids(
            @PathVariable String auctionRoomId,
            @PathVariable Integer auctionLaneId,
            @RequestBody AuctionLaneRequest auctionLaneRequest
    ) throws Exception {
        auctionLaneRequest.setAuctionRoomId(auctionRoomId);
        auctionLaneRequest.setAuctionLaneId(auctionLaneId);
        this.auctionLaneService.resolvePreBids(auctionLaneRequest);
    }
}
