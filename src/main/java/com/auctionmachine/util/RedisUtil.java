package com.auctionmachine.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
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
     * 取得後もデータは削除されない
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
     * RedisからJSONを取得し、指定のクラスにデシリアライズした後、データを削除する
     * 
     * @param key キー
     * @param clazz デシリアライズするクラス
     * @return 取得したオブジェクト（キーが存在しない場合はnull）
     */
    public <T> T pollObject(String key, Class<T> clazz) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json != null) {
                // データを取得した後に削除
                redisTemplate.delete(key);
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
    
    /**
     * Redis Streamsにメッセージを追加する
     * 
     * @param streamKey ストリームキー
     * @param object 追加するオブジェクト
     * @return 追加されたレコードのID
     */
    @SuppressWarnings("null")
    public String addToStream(String streamKey, Object object) {
        try {
            // オブジェクトをJSONに変換
            String json = objectMapper.writeValueAsString(object);
            
            // ストリームに追加するフィールドとして設定
            Map<String, String> fields = new HashMap<>();
            fields.put("data", json);
            
            // ストリームにメッセージを追加
            RecordId recordId = redisTemplate.opsForStream()
                    .add(MapRecord.create(streamKey, fields));
            
            return recordId.getValue();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Redis Streamsからメッセージを取得する（取得後もメッセージは残る）
     * 
     * @param <T> デシリアライズするクラスの型
     * @param streamKey ストリームキー
     * @param clazz デシリアライズするクラス
     * @param count 取得するメッセージの最大数
     * @return 取得したオブジェクトのリスト
     */
    public <T> List<T> getFromStream(String streamKey, Class<T> clazz, int count) {
        try {
            // ストリームからメッセージを読み取る
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                    .read(StreamReadOptions.empty().count(count), StreamOffset.create(streamKey, ReadOffset.from("0")));
            
            return deserializeStreamRecords(records, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Redis Streamsからメッセージを取得し、取得後にメッセージを削除する
     * 
     * @param <T> デシリアライズするクラスの型
     * @param streamKey ストリームキー
     * @param consumerGroup コンシューマーグループ名
     * @param consumerName コンシューマー名
     * @param clazz デシリアライズするクラス
     * @param count 取得するメッセージの最大数
     * @return 取得したオブジェクトのリスト
     */
    public <T> List<T> pollFromStream(String streamKey, String consumerGroup, String consumerName, 
                                     Class<T> clazz, int count) {
        try {
            // コンシューマーグループが存在しない場合は作成
            try {
                redisTemplate.opsForStream().createGroup(streamKey, consumerGroup);
            } catch (Exception e) {
                // グループが既に存在する場合は無視
            }
            
            // ストリームからメッセージを読み取る
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                    .read(Consumer.from(consumerGroup, consumerName),
                          StreamReadOptions.empty().count(count),
                          StreamOffset.create(streamKey, ReadOffset.lastConsumed()));
            
            // メッセージを確認（削除）
            for (MapRecord<String, Object, Object> record : records) {
                redisTemplate.opsForStream().acknowledge(streamKey, consumerGroup, record.getId());
            }
            
            return deserializeStreamRecords(records, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * ストリームレコードをデシリアライズする内部ヘルパーメソッド
     */
    private <T> List<T> deserializeStreamRecords(List<MapRecord<String, Object, Object>> records, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        
        for (MapRecord<String, Object, Object> record : records) {
            Map<Object, Object> value = record.getValue();
            if (value.containsKey("data")) {
                try {
                    String json = value.get("data").toString();
                    T obj = objectMapper.readValue(json, clazz);
                    result.add(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        return result;
    }
    
    /**
     * Redis Streamsのすべてのメッセージを削除する（ストリーム自体は残す）
     * 
     * @param streamKey ストリームキー
     * @return 削除されたメッセージの数
     */
    public long clearStream(String streamKey) {
        try {
            // ストリームの最小IDと最大IDを取得
            String minId = "0";
            String maxId = "+";
            
            // ストリームのすべてのメッセージを削除（XTRIM streamKey MAXLEN 0）
            return redisTemplate.opsForStream().trim(streamKey, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Redis Streamsのストリームキー自体を削除する
     * 
     * @param streamKey ストリームキー
     * @return 削除に成功した場合はtrue、失敗した場合はfalse
     */
    public boolean deleteStream(String streamKey) {
        try {
            return redisTemplate.delete(streamKey);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final String LIVE_BID_STREAM_KEY = "stream:live-bids";

}
