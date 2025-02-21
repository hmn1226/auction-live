package com.auctionmachine.resources.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auctionmachine.util.JwtUtil;

@Service
public class AuthService {
	// DBは使わずに固定ユーザで認証するため、UserRepositoryは削除（またはコメントアウト）
    // private final UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * DBを使わず、特定のメールアドレス＆パスワードを決め打ちで認証するサンプル。
     */
    public String login(String email, String rawPassword) {
        // もし入力された email / password が「固定の値」以外ならエラー
        if (!"test@example.com".equals(email) || !"testpass".equals(rawPassword)) {
            throw new RuntimeException("Invalid credentials");
        }

        // (1) 認証成功したら、UserDetailsを作る
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com") // 固定のユーザ名（メールアドレス）
                .password("")                     // ここはダミー。実際のパスワードは使わない
                .authorities(new ArrayList<>())   // 必要に応じて権限を付ける
                .build();

        // (2) JWT発行
        return jwtUtil.generateToken(userDetails);
    }
}