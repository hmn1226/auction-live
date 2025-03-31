package com.auctionmachine.core.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.auctionmachine.resources.model.AuctionRoomModel;
import com.auctionmachine.resources.repository.AuctionLaneRepository;
import com.auctionmachine.resources.repository.AuctionRoomRepository;
import com.auctionmachine.resources.repository.EntryRepository;
import com.auctionmachine.resources.service.AuctionLaneService;
import com.auctionmachine.resources.service.AuctionRoomService;
import com.auctionmachine.resources.service.EntryService;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 無限ループで監視を行うスレッドクラス
 * オークションルームの監視スレッドを起動・管理する
 */
@Component
@Data
@EqualsAndHashCode(callSuper=false)
public class InfiniteLoopObserveThread extends Thread implements ApplicationListener<ContextClosedEvent>, DisposableBean {
    
    private volatile boolean running = true;
		
	private Logger logger = LoggerFactory.getLogger(super.getClass());
	
	@Autowired
	private AuctionRoomRepository auctionRoomRepository;
	@Autowired
	private AuctionLaneRepository auctionLaneRepository;
	@Autowired
	private EntryRepository entryRepository;
	@Autowired
	private AuctionRoomService auctionRoomService;
	@Autowired
	private AuctionLaneService auctionLaneService;
	@Autowired
	private EntryService entryService;
	
	/**
	 * スレッド実行メソッド
	 * 定期的にオークションルームを監視し、各ルームの監視スレッドを起動する
	 */
	@Override
	public void run() {
		try {
			logger.info("無限ループ監視スレッド開始");
			
			while (running) {
				try {
					for (AuctionRoomModel model : auctionRoomRepository.get()) {
						logger.debug("オークションルーム検出: {}", model.getAuctionRoomId());
						
						// 各オークションルームの監視スレッドを起動
						//new AuctionRoomObserveThread(this, model).start();
					}
					
					// スレッド情報のログ出力（必要に応じてコメント解除）
					/*
					ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
					int threadCount = threadMXBean.getThreadCount();
					int daemonThreadCount = threadMXBean.getDaemonThreadCount();
					int peakThreadCount = threadMXBean.getPeakThreadCount();
					long totalStartedThreadCount = threadMXBean.getTotalStartedThreadCount();

					logger.info("現在のスレッド数: {}", threadCount);
					logger.info("デーモンスレッド数: {}", daemonThreadCount);
					logger.info("ピークスレッド数: {}", peakThreadCount);
					logger.info("合計開始スレッド数: {}", totalStartedThreadCount);
					*/
				} catch (Exception e) {
					logger.error("オークションルーム監視中にエラーが発生しました", e);
				}
				
				// 次の監視サイクルまで待機
				super.sleep(10000); // 10秒ごとにチェック
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.info("無限ループ監視スレッド中断");
		} catch (Exception e) {
			logger.error("無限ループ監視スレッドエラー", e);
		}
	}
	
	/**
	 * アプリケーションコンテキストが閉じられる際に呼び出されるメソッド
	 */
	@Override
	public void onApplicationEvent(@SuppressWarnings("null") ContextClosedEvent event) {
		stopThread();
	}
	
	/**
	 * Beanが破棄される際に呼び出されるメソッド
	 */
	@Override
	public void destroy() throws Exception {
		stopThread();
	}
	
	/**
	 * スレッドを安全に停止するメソッド
	 * アプリケーションのシャットダウン時やホットデプロイ時に呼び出される
	 */
	private void stopThread() {
		running = false;
		this.interrupt();
		logger.info("無限ループ監視スレッドを停止しました");
	}
}
