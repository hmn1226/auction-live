package com.auctionmachine.core.thread;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.auctionmachine.resources.model.AuctionLaneModel;
import com.auctionmachine.resources.model.AuctionRoomModel;
import com.auctionmachine.resources.repository.AuctionLaneRepository;
import com.auctionmachine.resources.repository.EntryRepository;
import com.auctionmachine.resources.schema.auctionlane.AuctionLaneMonitorResponse;
import com.auctionmachine.resources.service.AuctionLaneService;
import com.auctionmachine.web.socket.model.MessagingModel;
import com.auctionmachine.web.socket.service.MessagingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * オークションルームを監視するスレッドクラス
 * 各オークションレーンの状態を監視し、WebSocketを通じて状態更新を配信する
 */
@Component
@Data
@EqualsAndHashCode(callSuper=false)
public class AuctionRoomObserveThread extends Thread {
    
    private Logger logger = LoggerFactory.getLogger(super.getClass());
    
    private InfiniteLoopObserveThread infiniteLoopThread;
    private AuctionRoomModel auctionRoomModel;
    private AuctionLaneRepository auctionLaneRepository;
    private EntryRepository entryRepository;
    private AuctionLaneService auctionLaneService;
    
    /**
     * コンストラクタ
     * 
     * @param ilt 親スレッド
     * @param arm オークションルームモデル
     */
    public AuctionRoomObserveThread(InfiniteLoopObserveThread ilt, AuctionRoomModel arm) {
        this.infiniteLoopThread = ilt;
        this.auctionRoomModel = arm;
        this.auctionLaneRepository = ilt.getAuctionLaneRepository();
        this.entryRepository = ilt.getEntryRepository();
        this.auctionLaneService = ilt.getAuctionLaneService();
    }
    
    /**
     * スレッド実行メソッド
     * 各オークションレーンの監視スレッドを起動し、定期的に状態を送信する
     */
    @Override
    public void run() {
        try {
            logger.info("オークションルーム監視スレッド開始: {}", this.auctionRoomModel.getAuctionRoomId());
            
            // 各レーンの監視スレッドを起動
            for (AuctionLaneModel model : this.auctionLaneRepository.get(this.auctionRoomModel.getAuctionRoomId())) {
                new AuctionLaneObserveThread(this, model).start();
                logger.debug("レーン監視スレッド起動: {}-{}", model.getAuctionRoomId(), model.getAuctionLaneId());
            }
            
            // 定期的に状態を送信
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    send();
                    Thread.sleep(1000); // 1秒ごとに更新
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("オークションルーム監視スレッド中断: {}", this.auctionRoomModel.getAuctionRoomId());
                    break;
                } catch (Exception e) {
                    logger.error("オークションルーム監視エラー", e);
                }
            }
        } catch (Exception e) {
            logger.error("オークションルーム監視スレッド実行エラー", e);
        }
    }
    
    /**
     * WebSocketを通じて現在の状態を送信する
     * 
     * @throws JsonProcessingException JSON変換エラー
     */
    private void send() throws JsonProcessingException {
        MessagingService ms = MessagingService.getInstance();
        if (ms != null) {
            List<AuctionLaneMonitorResponse> response = new ArrayList<>();
            
            // 各レーンの状態を取得
            for (AuctionLaneModel model : this.auctionLaneRepository.get(this.auctionRoomModel.getAuctionRoomId())) {
                AuctionLaneMonitorResponse res = new AuctionLaneMonitorResponse();
                res.setAuctionRoomId(model.getAuctionRoomId());
                res.setAuctionLaneId(model.getAuctionLaneId());
                res.setAuctionLaneStatus(model.getAuctionLaneStatus());
                res.setCurrentEntryId(model.getCurrentEntryId());
                res.setCurrentPrice(model.getCurrentPrice());
                res.setCurrentHolderUserId(model.getCurrentHolderUserId());
                res.setLiveBidCount(model.getLiveBidCount());
                
                response.add(res);
            }
            
            // WebSocketでメッセージ送信
            MessagingModel message = new MessagingModel();
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(response);
            message.setContent(jsonString);
            
            // トピックにメッセージを送信
            String topic = "/topic/messages/" + this.auctionRoomModel.getAuctionRoomId();
            ms.sendMessage(topic, message);
            logger.debug("WebSocket送信: {}", topic);
        }
    }
}
