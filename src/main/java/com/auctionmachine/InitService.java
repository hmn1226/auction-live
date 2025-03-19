package com.auctionmachine;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auctionmachine.core.thread.InfiniteLoopObserveThread;

@Service
public class InitService {

    private final InfiniteLoopObserveThread infiniteLoopThread;

    @Autowired
    public InitService(InfiniteLoopObserveThread infiniteLoopThread) {
        this.infiniteLoopThread = infiniteLoopThread;
    }

    @PostConstruct
    public void init() {
        infiniteLoopThread.start(); // ここでSpring管理下のスレッドを起動
    }
}