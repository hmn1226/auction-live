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
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        logger.info("=== WebSocket Handshake Start ===");
        logger.info("Request URI: {}", request.getURI());

        // クエリパラメータからトークンを取得
        String query = request.getURI().getQuery();
        logger.info("Query Params: {}", query);

        if (query != null && query.contains("token=")) {
            String token = query.split("token=")[1];
            attributes.put("jwt_token", token);
            logger.info("JWT Token received: {}", token);
            return true;
        }
        
        logger.warn("No JWT Token found in query params. Rejecting WebSocket connection.");
        return false; // JWTがない場合は接続拒否
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        logger.info("=== WebSocket Handshake Complete ===");
    }
}