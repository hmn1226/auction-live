package com.auctionmachine.resources.model;

import java.time.Instant;

import com.auctionmachine.util.BeanUtil;

import lombok.Data;

/**
 * ライブ入札のモデルクラス
 * リアルタイムの入札情報を管理する
 */
@Data
public class LiveBidModel {
    // 基本情報
    private String auctionEntryId;
    private String bidUserId;
    private Instant bidTime;
    
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
