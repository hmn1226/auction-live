package com.auctionmachine.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auctionmachine.util.JwtUtil;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException{

		String authorizationHeader = request.getHeader("Authorization");
		String token = null;
		String ulid = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			token = authorizationHeader.substring(7);
			try {
	            ulid = jwtUtil.extractUsername(token);
	            
	        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401を返す
	            response.setContentType("application/json");
	            response.getWriter().write("{\"error\": \"ログイン有効期限が切れました。もう一度ログインしてください。\"}");
	            return; // ここでフィルター処理を中断
	        } catch (Exception ex) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.setContentType("application/json");
	            response.getWriter().write("{\"message\": \"ログインし直してください。\"}");
	            return;
	        }
		}        
		if (ulid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			List<String> roles = jwtUtil.extractRoles(token); // JWT から roles を取得

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(ulid, null, authorities);

			SecurityContextHolder.getContext().setAuthentication(authToken);
		}
		filterChain.doFilter(request, response);
	}
}