package com.auctionmachine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * オークションマシンアプリケーションのメインクラス
 * Spring Bootアプリケーションのエントリーポイント
 */
@SpringBootApplication
public class AuctionMachineApplication {

    /**
     * アプリケーションのメインメソッド
     * 
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(AuctionMachineApplication.class, args);
    }

}
