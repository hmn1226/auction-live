package com.auctionmachine.resources.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.auctionmachine.resources.model.AuctionLaneModel;
import com.auctionmachine.resources.model.EntryModel;
import com.auctionmachine.resources.model.LiveBidModel;
import com.auctionmachine.resources.model.PreBidModel;
import com.auctionmachine.resources.repository.AuctionLaneRepository;
import com.auctionmachine.resources.repository.EntryRepository;
import com.auctionmachine.resources.schema.auctionlane.AuctionLaneRequest;
import com.auctionmachine.resources.schema.auctionlane.CurrentPriceRequest;

/**
 * オークションレーンの操作を処理するサービスクラス
 * レーンのステータス管理、エントリー移動、入札処理などの機能を提供する
 */
@Service
public class AuctionLaneService {
    
    private final AuctionLaneRepository auctionLaneRepository;
    private final EntryRepository entryRepository;
    
    /**
     * コンストラクタインジェクション
     * 
     * @param auctionLaneRepository オークションレーンリポジトリ
     * @param entryRepository エントリーリポジトリ
     */
    public AuctionLaneService(AuctionLaneRepository auctionLaneRepository, EntryRepository entryRepository) {
        this.auctionLaneRepository = auctionLaneRepository;
        this.entryRepository = entryRepository;
    }
    
    /**
     * レーンのステータスを更新する
     * 
     * @param request ステータス更新リクエスト
     * @throws Exception リポジトリアクセス時の例外
     */
    public void status(AuctionLaneRequest request) throws Exception {
        AuctionLaneModel model = this.auctionLaneRepository.getById(request.getAuctionRoomId(), request.getAuctionLaneId());
        model.setAuctionLaneStatus(request.getAuctionLaneStatus());
        this.auctionLaneRepository.put(model);
    }
    
    /**
     * 現在価格を更新する
     * 
     * @param request 価格更新リクエスト
     * @throws Exception リポジトリアクセス時の例外
     */
    public void currentPrice(CurrentPriceRequest request) throws Exception {
        AuctionLaneModel model = this.auctionLaneRepository.getById(request.getAuctionRoomId(), request.getAuctionLaneId());
        model.setCurrentPrice(request.getCurrentPrice());
        this.auctionLaneRepository.put(model);
    }
    
    /**
     * 次のエントリーに移動する
     * 
     * @param request エントリー移動リクエスト
     * @throws Exception リポジトリアクセス時の例外
     */
    public void nextEntry(AuctionLaneRequest request) throws Exception {
        AuctionLaneModel model = this.auctionLaneRepository.getById(request.getAuctionRoomId(), request.getAuctionLaneId());
        if (model.getEntryIds().size() - 1 > model.getEntryListPointer()) {            
            model.setEntryListPointer(model.getEntryListPointer() + 1);
            this.auctionLaneRepository.put(model);
        }
    }
    
    /**
     * 前のエントリーに移動する
     * 
     * @param request エントリー移動リクエスト
     * @throws Exception リポジトリアクセス時の例外
     */
    public void prevEntry(AuctionLaneRequest request) throws Exception {
        AuctionLaneModel model = this.auctionLaneRepository.getById(request.getAuctionRoomId(), request.getAuctionLaneId());
        if (model.getEntryListPointer() > 0) {
            model.setEntryListPointer(model.getEntryListPointer() - 1);
            this.auctionLaneRepository.put(model);
        }
    }
    
    /**
     * ライブ入札を解決する（リクエストから）
     * 
     * @param request 入札解決リクエスト
     * @throws Exception リポジトリアクセス時の例外
     */
    public void resolveLiveBids(AuctionLaneRequest request) throws Exception {
        this.resolveLiveBids(request.getAuctionRoomId(), request.getAuctionLaneId());
    }
    
    /**
     * ライブ入札を解決する（IDから直接）
     * 
     * @param auctionRoomId オークションルームID
     * @param auctionLaneId オークションレーンID
     * @throws Exception リポジトリアクセス時の例外
     */
    public void resolveLiveBids(String auctionRoomId, Integer auctionLaneId) throws Exception {
        AuctionLaneModel model = this.auctionLaneRepository.getById(auctionRoomId, auctionLaneId);
        model.setLiveBidCount(model.getLiveBidQueue().size());
        
        if (model.getLiveBidCount() > 0) {
            String currentEntryId = model.getCurrentEntryId();
            
            if (currentEntryId != null) {
                LiveBidModel liveBid = null;
                EntryModel currentEntryModel = this.entryRepository.getById(currentEntryId);
                
                int i = 0;
                while ((liveBid = model.getLiveBidQueue().poll()) != null) {
                    if (i == 0) { // 最速の入札者が権利者
                        model.setCurrentPrice(currentEntryModel.getCurrentPrice() + model.getBidInterval());
                        model.setCurrentHolderUserId(liveBid.getBidUserId());
                    }
                    currentEntryModel.getBidLogList().add(liveBid); // キューから応札ログへ蓄積
                    i++;
                }
                
                // 現在価格の変更があった為PreBidの反映予約はクリアする
                clearPreBidReflectionData(model);
                
                // 変更を保存
                this.auctionLaneRepository.put(model);
                this.entryRepository.put(currentEntryModel);
            }
        }
    }
    
    /**
     * 事前入札の反映データをクリアする
     * 
     * @param model オークションレーンモデル
     */
    private void clearPreBidReflectionData(AuctionLaneModel model) {
        model.setPreBidReflectTimer(null);
        model.setPreBidReflectUserId(null);
        model.setTopPreBidUserId(null);
        model.setTopPreBidPrice(null);
        model.setSecondPreBidUserId(null);
        model.setSecondPreBidPrice(null);
    }
    
    /**
     * 事前入札を解決する（リクエストから）
     * 
     * @param request 入札解決リクエスト
     * @throws Exception リポジトリアクセス時の例外
     */
    public void resolvePreBids(AuctionLaneRequest request) throws Exception {
        this.resolvePreBids(request.getAuctionRoomId(), request.getAuctionLaneId());
    }
    
    /**
     * 事前入札を解決する（IDから直接）
     * 
     * @param auctionRoomId オークションルームID
     * @param auctionLaneId オークションレーンID
     * @throws Exception リポジトリアクセス時の例外
     */
    public void resolvePreBids(String auctionRoomId, Integer auctionLaneId) throws Exception {
        AuctionLaneModel model = this.auctionLaneRepository.getById(auctionRoomId, auctionLaneId);
        String currentEntryId = model.getCurrentEntryId();
        
        if (currentEntryId != null) {
            EntryModel currentEntryModel = this.entryRepository.getById(currentEntryId);
            
            if (model.getPreBidReflectTimer() != null) { // 事前入札反映タイマー作動中
                processPreBidReflectionTimer(model, currentEntryModel);
            } else {
                processPreBidList(model, currentEntryModel);
            }
            
            // 変更を保存
            this.auctionLaneRepository.put(model);
            this.entryRepository.put(currentEntryModel);
        }
    }
    
    /**
     * 事前入札反映タイマーを処理する
     * 
     * @param model オークションレーンモデル
     * @param currentEntryModel 現在のエントリーモデル
     */
    private void processPreBidReflectionTimer(AuctionLaneModel model, EntryModel currentEntryModel) {
        model.setPreBidReflectTimer(model.getPreBidReflectTimer() - 1);
        if (model.getPreBidReflectTimer() == 0) {
            LiveBidModel liveBid = new LiveBidModel();
            liveBid.setAuctionEntryId(currentEntryModel.getEntryId());
            liveBid.setBidUserId(model.getPreBidReflectUserId());
            liveBid.setBidTime(Instant.now());
            model.getLiveBidQueue().add(liveBid);
        }
    }
    
    /**
     * 事前入札リストを処理する
     * 
     * @param model オークションレーンモデル
     * @param currentEntryModel 現在のエントリーモデル
     */
    private void processPreBidList(AuctionLaneModel model, EntryModel currentEntryModel) {
        // 事前入札金額降順にソート
        currentEntryModel.getPreBidList().sort(Comparator.comparingInt(PreBidModel::getBidPrice).reversed());
        
        // 上位2件の事前入札を取得
        updateTopPreBids(model, currentEntryModel);
        
        // 事前入札の反映条件を確認
        checkPreBidReflectionConditions(model, currentEntryModel);
    }
    
    /**
     * 上位の事前入札情報を更新する
     * 
     * @param model オークションレーンモデル
     * @param currentEntryModel 現在のエントリーモデル
     */
    private void updateTopPreBids(AuctionLaneModel model, EntryModel currentEntryModel) {
        int i = 0;
        for (PreBidModel preBid : currentEntryModel.getPreBidList()) {
            switch (i) {
                case 0: // 1位事前入札
                    model.setTopPreBidUserId(preBid.getBidUserId());
                    model.setTopPreBidPrice(preBid.getBidPrice());
                    break;
                case 1: // 2位事前入札
                    model.setSecondPreBidUserId(preBid.getBidUserId());
                    model.setSecondPreBidPrice(preBid.getBidPrice());
                    break;
            }
            i++;
        }
    }
    
    /**
     * 事前入札の反映条件を確認する
     * 
     * @param model オークションレーンモデル
     * @param currentEntryModel 現在のエントリーモデル
     */
    private void checkPreBidReflectionConditions(AuctionLaneModel model, EntryModel currentEntryModel) {
        if (model.getTopPreBidUserId() != null && model.getTopPreBidPrice() != null) {
            if (model.getTopPreBidPrice() >= currentEntryModel.getCurrentPrice() + model.getBidInterval()) {
                // PreBidのユーザーIDが権利者ではない場合、もしくは2位入札者が居て2位入札金額が現在価格より高い場合
                if (!model.getTopPreBidUserId().equals(currentEntryModel.getCurrentHolderUserId()) ||
                    (model.getSecondPreBidUserId() != null && 
                     model.getSecondPreBidPrice() > currentEntryModel.getCurrentPrice())) {
                    Random random = new Random();
                    model.setPreBidReflectTimer(random.nextInt(9) + 1); // 1〜9回の中でランダムに反映させる
                    model.setPreBidReflectUserId(model.getTopPreBidUserId());
                }
            }
        }
    }
}
