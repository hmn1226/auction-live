package com.auctionmachine.resources.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auctionmachine.resources.schema.MessageResponse;

/**
 * ヘルスチェック用コントローラー
 * アプリケーションの稼働状態を確認するためのエンドポイントを提供する
 */
@RestController
public class HealthCheckController {
    
    /**
     * POSTリクエストによるヘルスチェック
     * 
     * @return 200 OKレスポンス
     * @throws Exception 例外発生時
     */
    @PostMapping("/api/health-check")
    public ResponseEntity<?> post() throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("OK"));
    }
    
    /**
     * GETリクエストによるヘルスチェック
     * 簡易的なヘルスチェック用に追加
     * 
     * @return 200 OKレスポンス
     * @throws Exception 例外発生時
     */
    @GetMapping("/api/health-check")
    public ResponseEntity<?> get() throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("OK"));
    }
}
