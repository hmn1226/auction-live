package com.auctionmachine.util;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RedisUtil {
	
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper; // JacksonのObjectMapperを使用

    /**
     * RedisにJSON形式でオブジェクトを保存
     * デフォルトでは有効期限なし（永続的に保存）
     */
    public void saveObject(String key, Object value) {
        try {
            String json = objectMapper.writeValueAsString(value); // オブジェクトをJSONに変換
            redisTemplate.opsForValue().set(key, json); // 有効期限なしで保存
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * RedisにJSON形式でオブジェクトを保存（有効期限付き）
     * 
     * @param key キー
     * @param value 保存するオブジェクト
     * @param timeout 有効期限の時間
     * @param unit 時間の単位（TimeUnit.DAYS, TimeUnit.HOURS など）
     */
    public void saveObject(String key, Object value, long timeout, TimeUnit unit) {
        try {
            String json = objectMapper.writeValueAsString(value); // オブジェクトをJSONに変換
            redisTemplate.opsForValue().set(key, json, timeout, unit); // 指定した有効期限で保存
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * RedisからJSONを取得し、指定のクラスにデシリアライズ
     */
    public <T> T getObject(String key, Class<T> clazz) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json != null) {
                return objectMapper.readValue(json, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 指定されたキーを削除
     */
    public void deleteObject(String key) {
        redisTemplate.delete(key);
    }

    /**
     * すべてのキーを取得
     */
    public Set<String> getAllKeys() {
        return redisTemplate.keys("*");
    }

    /**
     * 指定されたパターンに一致するキーをスキャン
     */
    public Set<String> scanKeys(String pattern) {
        Set<String> keys = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions().match(pattern + "*").build();
        try (@SuppressWarnings({ "null", "deprecation" })
        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options)) {
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next(), StandardCharsets.UTF_8)); // 文字エンコーディングを修正
            }
        }
        return keys;
    }
}
