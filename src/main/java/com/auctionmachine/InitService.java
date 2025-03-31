package com.auctionmachine;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auctionmachine.core.thread.InfiniteLoopObserveThread;

@Service
public class InitService {

    private final Logger logger = LoggerFactory.getLogger(InitService.class);
    private final InfiniteLoopObserveThread infiniteLoopThread;

    @Autowired
    public InitService(InfiniteLoopObserveThread infiniteLoopThread) {
        this.infiniteLoopThread = infiniteLoopThread;
    }

    @PostConstruct
    public void init() {
        // スレッドの設定
        infiniteLoopThread.setName("auction-room-observer");
        infiniteLoopThread.setDaemon(true); // デーモンスレッドとして設定
        
        logger.info("無限ループ監視スレッドを開始します");
        infiniteLoopThread.start(); // ここでSpring管理下のスレッドを起動
    }
}
