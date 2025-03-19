package com.auctionmachine.resources.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.stereotype.Component;

import com.auctionmachine.lib.AuctionLaneStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * オークションレーンのモデルクラス
 * レーンの状態、エントリー情報、入札情報などを管理する
 */
@Data
@Component
@JsonIgnoreProperties(ignoreUnknown = true) 
@EqualsAndHashCode(callSuper=false)
public class AuctionLaneModel extends BaseModel {
    // 基本情報
    private String auctionRoomId;
    private Integer auctionLaneId;    
    private AuctionLaneStatus auctionLaneStatus = AuctionLaneStatus.STOP;
    
    // 入札関連
    private Queue<LiveBidModel> liveBidQueue = new LinkedList<>();
    private int bidInterval = 1000;
    private int liveBidCount = 0;
    
    // エントリー関連
    private List<String> entryIds = new ArrayList<>();
    private int entryListPointer = 0;
    
    // 現在のエントリー情報
    private String currentEntryId;
    private String currentHolderUserId;
    private Integer currentPrice;
    
    // 事前入札関連
    private String topPreBidUserId = null;
    private Integer topPreBidPrice = null;
    private String secondPreBidUserId = null;
    private Integer secondPreBidPrice = null;
    private Integer preBidReflectTimer = null;
    private String preBidReflectUserId = null;
    
    /**
     * デフォルトコンストラクタ
     */
    public AuctionLaneModel() {
    }
    
    /**
     * モデルの一意のキーを取得する
     * 
     * @return オークションルームIDとレーンIDを組み合わせたキー
     */
    @Override
    public String getKey() {
        return this.auctionRoomId + "-" + this.auctionLaneId;
    }
    
    /**
     * エントリーリストを取得する
     * 互換性のために残しているメソッド
     * 
     * @return エントリーIDのリスト
     */
    public List<String> getEntryList() {
        return this.entryIds;
    }
    
    /**
     * エントリーリストを設定する
     * 互換性のために残しているメソッド
     * 
     * @param entryList エントリーIDのリスト
     */
    public void setEntryList(List<String> entryList) {
        this.entryIds = entryList;
    }
}
