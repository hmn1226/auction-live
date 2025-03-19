package com.auctionmachine.resources.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.auctionmachine.resources.exception.NotFoundException;
import com.auctionmachine.resources.model.BaseModel;
import com.auctionmachine.util.RedisUtil;

import lombok.Data;

@Data
public class BaseRepository {

	@Autowired
	RedisUtil redisUtil;
	
	String prefix;
	protected BaseRepository(String prefix){
		this.prefix = prefix;
	}
	
	protected <T> List<T> findAll(Class<T> clazz, String key) {
	    List<T> ret = new ArrayList<>();
	    Set<String> keys = this.redisUtil.scanKeys(key);
	    for (String _key : keys) {
	    	T model = this.redisUtil.getObject(_key, clazz);	        
	        ret.add(model);
	    }
	    return ret;
	}
	
	protected <T> T findById(Class<T> clazz, String key) throws NotFoundException{
	    T model = this.redisUtil.getObject(key, clazz);
	    if (model == null) {
	        throw new NotFoundException("not found: " + key);
	    }
	    return model;
	}
	
	protected void save(BaseModel model) {
		this.redisUtil.saveObject(this.prefix+":"+model.getKey(),model);
	}
	
	protected void save(List<? extends BaseModel>  models) {
		for(BaseModel model : models) {
			this.save(model);
		}
	}
	
	protected void deleteById(String key) {
		this.redisUtil.deleteObject(this.prefix+":"+key);
	}
	
	protected void delete(BaseModel model) {
		this.redisUtil.deleteObject(this.prefix+":"+model.getKey());
	}
}
