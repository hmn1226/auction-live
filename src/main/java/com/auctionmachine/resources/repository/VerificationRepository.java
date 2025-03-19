package com.auctionmachine.resources.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.auctionmachine.resources.model.VerificationModel;
import com.auctionmachine.util.RedisUtil;

@Repository
public class VerificationRepository {
	
	@Autowired
    private RedisUtil redisUtil;

    public VerificationModel findByVerificationToken(String verificationToken) {
    	System.out.println("verification:" + verificationToken);
        return (VerificationModel) this.redisUtil.getObject("verification:" + verificationToken,VerificationModel.class);
    }
    public void save(VerificationModel authModel) {
        this.redisUtil.saveObject("verification:" + authModel.getVerificationToken(), authModel);
    }
    public void deleteByVerificationToken(String email) {
        this.redisUtil.deleteObject("verification:" + email);
    }
}