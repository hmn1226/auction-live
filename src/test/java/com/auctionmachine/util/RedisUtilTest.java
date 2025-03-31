package com.auctionmachine.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class RedisUtilTest {

    @Autowired
    private RedisUtil redisUtil;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private StreamOperations<String, Object, Object> streamOperations;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setup() {
        // StreamOperationsのモックを設定
        streamOperations = org.mockito.Mockito.mock(StreamOperations.class);
        when(redisTemplate.opsForStream()).thenReturn(streamOperations);
    }

    @Test
    public void testGetFromStream() {
        // テスト用のデータ
        String streamKey = RedisUtil.LIVE_BID_STREAM_KEY;
        int count = 10;
        
        // ここでStreamOperationsのreadメソッドの戻り値をモックする
        // 実際のテストでは、適切なMapRecordのリストを返すようにモックを設定する
        
        // getFromStreamメソッドを呼び出す
        List<TestData> result = redisUtil.getFromStream(streamKey, TestData.class, count);
        
        // 結果を検証
        // 実際のテストでは、期待される結果と比較する
        assertNotNull(result);
    }

    @Test
    public void testPollFromStream() {
        // テスト用のデータ
        String streamKey = RedisUtil.LIVE_BID_STREAM_KEY;
        String consumerGroup = "testGroup";
        String consumerName = "testConsumer";
        int count = 10;
        
        // ここでStreamOperationsのreadメソッドとacknowledgeメソッドの戻り値をモックする
        
        // pollFromStreamメソッドを呼び出す
        List<TestData> result = redisUtil.pollFromStream(streamKey, consumerGroup, consumerName, TestData.class, count);
        
        // 結果を検証
        assertNotNull(result);
    }
    
    // テスト用のデータクラス
    public static class TestData {
        private String id;
        private String name;
        
        public TestData() {}
        
        public TestData(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
}
