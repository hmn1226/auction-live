package com.auctionmachine.util;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {
	
	@Autowired
    private RedisTemplate<String, Object> redisTemplate;
	
	public void saveObject(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    public Object getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    public void deleteObject(String key) {
        redisTemplate.delete(key);
    }
    public Set<String> getAllKeys() {
        return redisTemplate.keys("*");
    }    
    public Set<String> scanKeys(String pattern) {
        Set<String> keys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions().match(pattern + "*").build();
        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options)) {
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
        }
        return keys;
    }
}
