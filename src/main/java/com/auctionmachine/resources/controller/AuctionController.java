package com.auctionmachine.resources.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auctionmachine.resources.schema.auction.AuctionRequest;
import com.auctionmachine.resources.schema.auction.AuctionResponse;
import com.auctionmachine.resources.schema.auction.AuctionsResponse;
import com.auctionmachine.resources.service.AuctionRoomService;
import com.auctionmachine.resources.service.AuctionService;

@RestController
public class AuctionController {
    
    private final AuctionService auctionService;
    private final AuctionRoomService auctionRoomService;
    
    public AuctionController(AuctionService auctionService, AuctionRoomService auctionRoomService) {
        this.auctionService = auctionService;
        this.auctionRoomService = auctionRoomService;
    }
	
    @GetMapping("/api/auctions")
    public AuctionsResponse getPublic() {
        return this.auctionService.getPublic();
    }
    
    @GetMapping("/api/auctions/{auctionRoomId}")
    public AuctionResponse getPublicById(@PathVariable String auctionRoomId) throws Exception {
        return this.auctionService.getPublicById(auctionRoomId);
    }
    
    @PutMapping("/api/auctions/{auctionRoomId}")
    public AuctionResponse put(
            @PathVariable String auctionRoomId,
            @RequestBody AuctionRequest auctionRequest) throws Exception {
        auctionRequest.setAuctionRoomId(auctionRoomId);
        return this.auctionService.put(auctionRequest);
    }
    
    @GetMapping("/api/auctions/auction-room-id/generate")
    public Map<String, String> generate() throws Exception {
        return this.auctionRoomService.generate();
    }
    
    /**
     * 管理者用APIエンドポイント
     */
    @GetMapping("/api/admin/auctions")
    public AuctionsResponse getFull() {
        return this.auctionService.getFull();
    }
    
    @GetMapping("/api/admin/auctions/{auctionRoomId}")
    public AuctionResponse getFullById(@PathVariable String auctionRoomId) throws Exception {
        return this.auctionService.getFullById(auctionRoomId);
    }

    @DeleteMapping("/api/admin/auctions/{auctionRoomId}")
    public void delete(@PathVariable String auctionRoomId) throws Exception {
        this.auctionService.delete(auctionRoomId);
    }

}
