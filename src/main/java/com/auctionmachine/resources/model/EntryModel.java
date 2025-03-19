package com.auctionmachine.resources.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * オークションエントリーのモデルクラス
 * 出品商品の情報、価格情報、入札情報などを管理する
 */
@Data
@Component
@JsonIgnoreProperties(ignoreUnknown = true) 
@EqualsAndHashCode(callSuper=false)
public class EntryModel extends BaseModel {
    // 基本情報
    private String auctionRoomId;
    private Integer auctionLaneId;
    private String entryId;
    private String entryName;
    private String entryDescription;
    
    // 価格情報
    private Integer startPrice;
    private Integer slowPrice;
    private Integer reservePrice;
    private Integer currentPrice;
    
    // 数量情報
    private Integer quantity;
    private Integer bulkSaleMode;
    
    // 入札情報
    private List<PreBidModel> preBidList = new ArrayList<>();
    private List<LiveBidModel> liveBidList = new ArrayList<>();
    private List<LiveBidModel> bidLogList = new ArrayList<>();
    private String currentHolderUserId;
    
    // 画像情報
    private List<EntryImageModel> entryImages = new ArrayList<>();
    
    /**
     * デフォルトコンストラクタ
     */
    public EntryModel() {
    }
    
    /**
     * モデルの一意のキーを取得する
     * 
     * @return エントリーID
     */
    @Override
    public String getKey() {
        return this.entryId;
    }
    
    /**
     * エントリー画像のモデルクラス
     */
    @Data
    public static class EntryImageModel {
        private String path;
        private String data;
    }
}
