package com.auctionmachine.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auctionmachine.util.JwtUtil;

@Component
public class JwtFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	public JwtFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String token = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
			if (!jwtUtil.validateToken(token)) {
				response.sendError(HttpStatus.UNAUTHORIZED.value(), "トークンが無効");
				return;
			}
		} else {
			response.sendError(HttpStatus.UNAUTHORIZED.value(), "トークンが必要");
			return;
		}
		chain.doFilter(request, response);
	}
}