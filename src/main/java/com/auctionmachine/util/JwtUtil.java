package com.auctionmachine.util;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

	@Value("${jwt.secret-key}")
	private String secretKey;
	
	@Value("${jwt.expiration-time}")
	private long expirationTime;

	public String generateToken(UserDetails userDetails) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expirationTime);
		return Jwts.builder()
				.setSubject(userDetails.getUsername()) // 通常はここにユーザ名やIDなどをセット
				.claim("role", userDetails.getAuthorities().stream()
			            .map(GrantedAuthority::getAuthority) // ここで "ROLE_ADMIN" などを取得
			            .collect(Collectors.toList()))       // List で格納する
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}

	public String extractUsername(String token) {
		return Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
	public List<String> extractRoles(String token) {
	    Claims claims = Jwts.parser()
	        .setSigningKey(secretKey)
	        .parseClaimsJws(token)
	        .getBody();

	    return claims.get("role", List.class); // List<String> 形式で取得
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		Date expiration = Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token)
				.getBody()
				.getExpiration();
		return expiration.before(new Date());
	}
}
