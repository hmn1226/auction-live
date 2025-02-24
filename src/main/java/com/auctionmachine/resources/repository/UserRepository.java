package com.auctionmachine.resources.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.auctionmachine.resources.model.UserModel;
import com.auctionmachine.util.RedisUtil;

@Repository
public class UserRepository {
	
	@Autowired
    private RedisUtil redisUtil;

    public UserModel findByEmail(String email) {
        return (UserModel) this.redisUtil.getObject("user:" + email);
    }
    public List<UserModel> findAll() {
    	List<UserModel> ret = new ArrayList<>();
    	for(String key : this.redisUtil.scanKeys("user:")) {
    		ret.add((UserModel)this.redisUtil.getObject(key));
    	}
        return ret;
    }
    public void save(UserModel userModel) {
        this.redisUtil.saveObject("user:" + userModel.getEmail(), userModel);
    }
    public void deleteByEmail(String email) {
        this.redisUtil.deleteObject("user:" + email);
    }
}