package com.auctionmachine.resources.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auctionmachine.util.RedisUtil;

/**
 * RedisUtilのメソッドを呼び出すAPIを提供するコントローラー
 */
@RestController
@RequestMapping("/api/redis")
public class RedisController {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 指定されたストリームキーからメッセージを取得する
     * 
     * @param streamKey ストリームキー
     * @param count 取得するメッセージの最大数（デフォルト: 10）
     * @return メッセージのリスト
     */
    @GetMapping("/stream/{streamKey}")
    public ResponseEntity<List<Map>> getFromStream(
            @PathVariable String streamKey,
            @RequestParam(defaultValue = "10") int count) {
        
        List<Map> messages = redisUtil.getFromStream(streamKey, Map.class, count);
        return ResponseEntity.ok(messages);
    }

    /**
     * 指定されたストリームキーにメッセージを追加する
     * 
     * @param streamKey ストリームキー
     * @param message 追加するメッセージ
     * @return 追加されたレコードのID
     */
    @PostMapping("/stream/{streamKey}")
    public ResponseEntity<String> addToStream(
            @PathVariable String streamKey,
            @RequestBody Map<String, Object> message) {
        
        String recordId = redisUtil.addToStream(streamKey, message);
        return ResponseEntity.ok(recordId);
    }

    /**
     * 指定されたストリームキーからメッセージを取得し、取得後にメッセージを削除する
     * 
     * @param streamKey ストリームキー
     * @param consumerGroup コンシューマーグループ名（デフォルト: "api-group"）
     * @param consumerName コンシューマー名（デフォルト: "api-consumer"）
     * @param count 取得するメッセージの最大数（デフォルト: 10）
     * @return メッセージのリスト
     */
    @GetMapping("/stream/{streamKey}/poll")
    public ResponseEntity<List<Map>> pollFromStream(
            @PathVariable String streamKey,
            @RequestParam(defaultValue = "api-group") String consumerGroup,
            @RequestParam(defaultValue = "api-consumer") String consumerName,
            @RequestParam(defaultValue = "10") int count) {
        
        List<Map> messages = redisUtil.pollFromStream(
                streamKey, consumerGroup, consumerName, Map.class, count);
        return ResponseEntity.ok(messages);
    }

    /**
     * 指定されたストリームキーのすべてのメッセージを削除する（ストリーム自体は残す）
     * 
     * @param streamKey ストリームキー
     * @return 削除されたメッセージの数
     */
    @DeleteMapping("/stream/{streamKey}")
    public ResponseEntity<Long> clearStream(@PathVariable String streamKey) {
        long deletedCount = redisUtil.clearStream(streamKey);
        return ResponseEntity.ok(deletedCount);
    }

    /**
     * 指定されたストリームキー自体を削除する
     * 
     * @param streamKey ストリームキー
     * @return 削除に成功した場合はtrue、失敗した場合はfalse
     */
    @DeleteMapping("/stream/{streamKey}/delete")
    public ResponseEntity<Boolean> deleteStream(@PathVariable String streamKey) {
        boolean deleted = redisUtil.deleteStream(streamKey);
        return ResponseEntity.ok(deleted);
    }

    /**
     * 通常のRedisキーからオブジェクトを取得する
     * 
     * @param key Redisキー
     * @return 取得したオブジェクト
     */
    @GetMapping("/key/{key}")
    public ResponseEntity<Map<String, Object>> getObject(@PathVariable String key) {
        Map<String, Object> value = redisUtil.getObject(key, Map.class);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    /**
     * 通常のRedisキーからオブジェクトを取得し、取得後にデータを削除する
     * 
     * @param key Redisキー
     * @return 取得したオブジェクト
     */
    @GetMapping("/key/{key}/poll")
    public ResponseEntity<Map<String, Object>> pollObject(@PathVariable String key) {
        Map<String, Object> value = redisUtil.pollObject(key, Map.class);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    /**
     * 通常のRedisキーにオブジェクトを保存する
     * 
     * @param key Redisキー
     * @param value 保存するオブジェクト
     * @return 保存に成功した場合は"OK"
     */
    @PostMapping("/key/{key}")
    public ResponseEntity<String> saveObject(
            @PathVariable String key,
            @RequestBody Map<String, Object> value) {
        
        redisUtil.saveObject(key, value);
        return ResponseEntity.ok("OK");
    }

    /**
     * 通常のRedisキーを削除する
     * 
     * @param key Redisキー
     * @return 削除に成功した場合は"OK"
     */
    @DeleteMapping("/key/{key}")
    public ResponseEntity<String> deleteObject(@PathVariable String key) {
        redisUtil.deleteObject(key);
        return ResponseEntity.ok("OK");
    }
}
