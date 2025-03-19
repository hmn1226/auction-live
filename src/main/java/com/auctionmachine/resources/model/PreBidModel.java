package com.auctionmachine.resources.model;

import java.time.Instant;

import com.auctionmachine.util.BeanUtil;

import lombok.Data;

/**
 * 事前入札のモデルクラス
 * オークション開始前の入札情報を管理する
 */
@Data
public class PreBidModel {
    // 基本情報
    private String auctionEntryId;
    private String bidUserId;
    private Instant bidTime;
    private Integer bidPrice;
    
    /**
     * オブジェクトの文字列表現を取得する
     * 
     * @return オブジェクトの文字列表現
     */
    @Override
    public String toString() {
        return BeanUtil.describe(this);
    }
}
