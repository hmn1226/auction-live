package com.auctionmachine.web.socket.model;

import lombok.Data;

/**
 * WebSocketメッセージングのモデルクラス
 * WebSocketを通じて送受信されるメッセージの構造を定義する
 */
@Data
public class MessagingModel {
    
    // メッセージの種類
    private String type;
    
    // メッセージの内容（JSON文字列）
    private String content;
    
    // 送信者情報
    private String sender;
    
    // タイムスタンプ
    private long timestamp;
    
    /**
     * デフォルトコンストラクタ
     */
    public MessagingModel() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * コンテンツを指定するコンストラクタ
     * 
     * @param content メッセージ内容
     */
    public MessagingModel(String content) {
        this();
        this.content = content;
    }
    
    /**
     * タイプとコンテンツを指定するコンストラクタ
     * 
     * @param type メッセージタイプ
     * @param content メッセージ内容
     */
    public MessagingModel(String type, String content) {
        this(content);
        this.type = type;
    }
}
