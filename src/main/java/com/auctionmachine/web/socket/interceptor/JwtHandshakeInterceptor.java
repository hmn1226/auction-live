package com.auctionmachine.web.socket.interceptor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(
            @SuppressWarnings("null") ServerHttpRequest request, 
            @SuppressWarnings("null") ServerHttpResponse response,
            @SuppressWarnings("null") WebSocketHandler wsHandler, 
            @SuppressWarnings("null") Map<String, Object> attributes
    ) {
        logger.info("=== WebSocket Handshake Start ===");
        logger.info("Request URI: {}", request.getURI());

        // クエリパラメータからトークンを取得
        String query = request.getURI().getQuery();
        logger.debug("Query Params received"); // 機密情報を含まないログに変更

        if (query != null && query.contains("token=")) {
            String token = query.split("token=")[1];
            attributes.put("jwt_token", token);
            logger.info("JWT Token validation successful"); // トークン自体をログに出力しない
            return true;
        }
        
        logger.warn("No JWT Token found in query params. Rejecting WebSocket connection.");
        return false; // JWTがない場合は接続拒否
    }

    @Override
    public void afterHandshake(
            @SuppressWarnings("null") ServerHttpRequest request, 
            @SuppressWarnings("null") ServerHttpResponse response,
            @SuppressWarnings("null") WebSocketHandler wsHandler, 
            @SuppressWarnings("null") Exception exception
    ) {
        logger.info("=== WebSocket Handshake Complete ===");
    }
}
