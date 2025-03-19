package com.auctionmachine.core.queue;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import lombok.Data;

/**
 * 入札キューを管理するクラス
 * 入札情報を一時的に保持するためのキュー構造を提供する
 */
public class BidQueue {
    
    private static final Queue<Map<String, BidBean>> bidQueue = new LinkedList<>();
    
    /**
     * キューに入札情報を追加する
     * 
     * @param bidInfo 入札情報を含むマップ
     */
    public static void addBid(Map<String, BidBean> bidInfo) {
        bidQueue.add(bidInfo);
    }
    
    /**
     * キューから入札情報を取得して削除する
     * 
     * @return 入札情報を含むマップ、キューが空の場合はnull
     */
    public static Map<String, BidBean> pollBid() {
        return bidQueue.poll();
    }
    
    /**
     * キューが空かどうかを確認する
     * 
     * @return キューが空の場合はtrue、そうでない場合はfalse
     */
    public static boolean isEmpty() {
        return bidQueue.isEmpty();
    }
    
    /**
     * キューのサイズを取得する
     * 
     * @return キューのサイズ
     */
    public static int size() {
        return bidQueue.size();
    }
    
    /**
     * 入札情報を保持するBeanクラス
     */
    @Data
    public static class BidBean {
        private String bidderUlid;
        private Integer bidPrice;
    }
}
