package com.auctionmachine.security;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auctionmachine.resources.service.UserRepository;
import com.auctionmachine.util.JwtUtil;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException{
        // HTTPヘッダから"Authorization"を取得
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        // "Bearer " で始まる場合はJWTトークンを切り出す
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            // トークンから email を抽出
            try {
                email = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                // トークンが不正な場合の対処
            }
        }

        // email が取れていて、SecurityContextHolderに認証情報がない場合
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // ユーザをDBから取得
            User userEntity = userRepository.findByEmail(email).orElse(null);

            if (userEntity != null) {
                // UserDetails を作成
                UserDetails userDetails = 
                        org.springframework.security.core.userdetails.User
                                .withUsername(userEntity.getEmail())
                                .password(userEntity.getPassword())
                                .authorities(new ArrayList<>())
                                .build();

                // トークンが有効な場合は認証情報を設定
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    // コンテキストに認証情報を登録
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // 次のフィルタへ
        filterChain.doFilter(request, response);
    }
}