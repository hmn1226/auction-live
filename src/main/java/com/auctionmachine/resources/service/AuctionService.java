package com.auctionmachine.resources.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.auctionmachine.resources.model.AuctionLaneModel;
import com.auctionmachine.resources.model.AuctionRoomModel;
import com.auctionmachine.resources.model.EntryModel;
import com.auctionmachine.resources.model.EntryModel.EntryImageModel;
import com.auctionmachine.resources.repository.AuctionLaneRepository;
import com.auctionmachine.resources.repository.AuctionRoomRepository;
import com.auctionmachine.resources.repository.EntryRepository;
import com.auctionmachine.resources.schema.auction.AuctionRequest;
import com.auctionmachine.resources.schema.auction.AuctionResponse;
import com.auctionmachine.resources.schema.auction.AuctionSchema;
import com.auctionmachine.resources.schema.auction.AuctionSchema.FullEntry;
import com.auctionmachine.resources.schema.auction.AuctionSchema.Image;
import com.auctionmachine.resources.schema.auction.AuctionsResponse;
import com.auctionmachine.util.BeanUtil;

import de.huxhorn.sulky.ulid.ULID;

@Service
public class AuctionService {
    
    private final AuctionRoomRepository auctionRoomRepository;
    private final AuctionLaneRepository auctionLaneRepository;
    private final EntryRepository entryRepository;
    
    public AuctionService(
            AuctionRoomRepository auctionRoomRepository,
            AuctionLaneRepository auctionLaneRepository,
            EntryRepository entryRepository) {
        this.auctionRoomRepository = auctionRoomRepository;
        this.auctionLaneRepository = auctionLaneRepository;
        this.entryRepository = entryRepository;
    }

    /**
     * オークションルーム一覧を取得する（管理者用）
     * 
     * @return オークションルーム一覧
     */
    public AuctionsResponse getFull() {
        return getAuctions();
    }
    
    /**
     * オークションルーム一覧を取得する（一般ユーザー用）
     * 
     * @return オークションルーム一覧
     */
    public AuctionsResponse getPublic() {
        return getAuctions();
    }
    
    /**
     * オークションルーム一覧を取得する共通メソッド
     * 
     * @return オークションルーム一覧
     */
    private AuctionsResponse getAuctions() {
        AuctionsResponse response = new AuctionsResponse();
        List<AuctionRoomModel> auctionRoomModels = this.auctionRoomRepository.get();
        
        for (AuctionRoomModel auctionRoomModel : auctionRoomModels) {
            AuctionSchema auctionSchema = new AuctionSchema();
            auctionSchema.setAuctionRoomId(auctionRoomModel.getAuctionRoomId());
            auctionSchema.setLiverUlid(auctionRoomModel.getLiverUlid());
            response.getAuctions().add(auctionSchema);
        }
        
        return response;
    }
	
    /**
     * 指定されたオークションルームの詳細情報を取得する（管理者用）
     * 
     * @param auctionRoomId オークションルームID
     * @return オークションルームの詳細情報
     * @throws Exception リポジトリアクセス時の例外
     */
    public AuctionResponse getFullById(String auctionRoomId) throws Exception {
        AuctionResponse response = getAuctionResponseBase(auctionRoomId);
        response.addFullEntryModels(getEntryModels(auctionRoomId));
        return response;
    }
    
    /**
     * 指定されたオークションルームの詳細情報を取得する（一般ユーザー用）
     * 
     * @param auctionRoomId オークションルームID
     * @return オークションルームの詳細情報
     * @throws Exception リポジトリアクセス時の例外
     */
    public AuctionResponse getPublicById(String auctionRoomId) throws Exception {
        AuctionResponse response = getAuctionResponseBase(auctionRoomId);
        response.addPublicEntryModels(getEntryModels(auctionRoomId));
        return response;
    }
    
    /**
     * オークションレスポンスの基本情報を取得する共通メソッド
     * 
     * @param auctionRoomId オークションルームID
     * @return オークションレスポンスの基本情報
     * @throws Exception リポジトリアクセス時の例外
     */
    private AuctionResponse getAuctionResponseBase(String auctionRoomId) throws Exception {
        AuctionRoomModel auctionRoomModel = this.auctionRoomRepository.getById(auctionRoomId);
        List<AuctionLaneModel> auctionLaneModels = this.auctionLaneRepository.get(auctionRoomId);
        
        AuctionResponse response = new AuctionResponse();
        response.setAuctionRoomModel(auctionRoomModel);
        response.addAuctionLaneModels(auctionLaneModels);
        
        return response;
    }
    
    /**
     * 指定されたオークションルームのエントリーモデルリストを取得する
     * 
     * @param auctionRoomId オークションルームID
     * @return エントリーモデルリスト
     * @throws Exception リポジトリアクセス時の例外
     */
    private List<EntryModel> getEntryModels(String auctionRoomId) throws Exception {
        List<AuctionLaneModel> auctionLaneModels = this.auctionLaneRepository.get(auctionRoomId);
        List<EntryModel> entryModels = new ArrayList<>();
        
        for (AuctionLaneModel auctionLaneModel : auctionLaneModels) {
            List<String> entryIds = auctionLaneModel.getEntryIds();
            for (String entryId : entryIds) {
                entryModels.add(this.entryRepository.getById(entryId));
            }
        }
        
        return entryModels;
    }
	
    /**
     * オークションルームを登録または更新する
     * 
     * @param auctionRequest オークションリクエスト
     * @return オークションレスポンス
     * @throws Exception リポジトリアクセス時の例外
     */
    public AuctionResponse put(AuctionRequest auctionRequest) throws Exception {
        // 認証情報からライバーULIDを取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String liverUlid = authentication.getName();
        auctionRequest.setLiverUlid(liverUlid);

        // オークションルームモデルの作成
        AuctionRoomModel auctionRoomModel = new AuctionRoomModel();
        auctionRoomModel.setAuctionRoomId(auctionRequest.getAuctionRoomId());
        auctionRoomModel.setLiverUlid(auctionRequest.getLiverUlid());
        auctionRoomModel.setStartDatetime(auctionRequest.getStartDatetime());
        auctionRoomModel.setEndDatetime(auctionRequest.getEndDatetime());
        auctionRoomModel.setAuctionType(auctionRequest.getAuctionType());
        auctionRoomModel.setPublicMode(auctionRequest.getPublicMode());
        auctionRoomModel.setAutoPilotMode(auctionRequest.getAutoPilotMode());
        auctionRoomModel.setLaneAmount(auctionRequest.getLaneAmount());
        auctionRoomModel.setPriceInterval(auctionRequest.getPriceInterval());

        // 現在は1レーンのみ対応（複数レーン対応は今後実装予定）
        AuctionLaneModel auctionLaneModel = new AuctionLaneModel();
        auctionLaneModel.setAuctionRoomId(auctionRequest.getAuctionRoomId());
        auctionLaneModel.setAuctionLaneId(0);
        
        // エントリーモデルの作成
        List<EntryModel> entryModels = new ArrayList<>();
        for (FullEntry fullEntry : auctionRequest.getFullEntries()) {
            EntryModel entryModel = createEntryModel(auctionRequest, fullEntry);
            auctionLaneModel.getEntryIds().add(entryModel.getEntryId());
            entryModels.add(entryModel);
        }
        
        // リポジトリに保存
        this.auctionRoomRepository.put(auctionRoomModel);
        this.auctionLaneRepository.put(auctionLaneModel);
        this.entryRepository.put(entryModels);
        
        return BeanUtil.deepCopy(AuctionResponse.class, auctionRequest);
    }
    
    /**
     * エントリーモデルを作成する
     * 
     * @param auctionRequest オークションリクエスト
     * @param fullEntry エントリー情報
     * @return エントリーモデル
     */
    private EntryModel createEntryModel(AuctionRequest auctionRequest, FullEntry fullEntry) {
        EntryModel entryModel = new EntryModel();
        entryModel.setAuctionRoomId(auctionRequest.getAuctionRoomId());
        entryModel.setAuctionLaneId(0);
        
        // エントリーIDの設定（新規の場合はULIDを生成）
        if (fullEntry.getEntryId() == null) {
            ULID ulid = new ULID();
            String entryId = ulid.nextULID();
            entryModel.setEntryId(entryId);
        } else {
            entryModel.setEntryId(fullEntry.getEntryId());
        }
        
        // 基本情報の設定
        entryModel.setEntryName(fullEntry.getEntryName());
        entryModel.setEntryDescription(fullEntry.getEntryDescription());
        entryModel.setStartPrice(fullEntry.getStartPrice());
        entryModel.setSlowPrice(fullEntry.getSlowPrice());
        entryModel.setReservePrice(fullEntry.getReservePrice());
        entryModel.setQuantity(fullEntry.getQuantity());
        entryModel.setBulkSaleMode(fullEntry.getBulkSaleMode());
        entryModel.setCurrentPrice(fullEntry.getStartPrice()); // 初期値はスタート価格
        
        // 画像情報の設定
        for (Image image : fullEntry.getImages()) {
            EntryImageModel entryImageModel = new EntryImageModel();
            entryImageModel.setPath(image.getPath());
            entryImageModel.setData(image.getData());
            entryModel.getEntryImages().add(entryImageModel);
        }
        
        return entryModel;
    }
    
    /**
     * オークションルームを削除する
     * 関連するレーンとエントリーも削除する
     * 
     * @param auctionRoomId オークションルームID
     * @throws Exception リポジトリアクセス時の例外
     */
    public void delete(String auctionRoomId) throws Exception {
        // オークションルームに関連するレーンを取得
        List<AuctionLaneModel> auctionLaneModels = this.auctionLaneRepository.get(auctionRoomId);
        
        // 各レーンに関連するエントリーを削除
        for (AuctionLaneModel auctionLaneModel : auctionLaneModels) {
            for (String entryId : auctionLaneModel.getEntryIds()) {
                this.entryRepository.delete(entryId);
            }
            // レーンを削除
            this.auctionLaneRepository.delete(auctionRoomId, auctionLaneModel.getAuctionLaneId());
        }
        
        // オークションルームを削除
        this.auctionRoomRepository.delete(auctionRoomId);
    }
}
