package com.auctionmachine.resources.schema.auctionlane;

import com.auctionmachine.lib.AuctionLaneStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * オークションレーンの監視情報レスポンスクラス
 * WebSocketを通じて送信されるレーンの状態情報を保持する
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuctionLaneMonitorResponse {
    
    // 基本情報
    private String auctionRoomId;
    private Integer auctionLaneId;
    private AuctionLaneStatus auctionLaneStatus;
    
    // 現在のエントリー情報
    private String currentEntryId;
    private Integer currentPrice;
    private String currentHolderUserId;
    
    // 入札情報
    private int liveBidCount;
    private Integer bidInterval;
    
    // タイマー情報
    private Long remainingTime;
    private Boolean isCountingDown;
    
    /**
     * デフォルトコンストラクタ
     */
    public AuctionLaneMonitorResponse() {
    }
}
