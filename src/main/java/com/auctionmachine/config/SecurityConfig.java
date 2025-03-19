package com.auctionmachine.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.auctionmachine.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		// (1) CSRFを無効化 (API 利用の場合)
		.csrf(csrf -> csrf.disable())

		// (2) CORS を有効化
		.cors(cors -> cors.configurationSource(corsConfigurationSource()))

		// (3) セッションをステートレスにする (JWT利用のため)
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

		// (4) 認証ルールを定義
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/**").permitAll()  // ログイン・認証 API は許可
				.requestMatchers("/api/auction/**").permitAll()
				.requestMatchers("/gs-guide-websocket/**").permitAll() // WebSocketを許可
				.requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")  // ADMINのみ
				.anyRequest().authenticated()  // それ以外の API は認証必須
		)

		// (5) JWT フィルターを適用
		.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// React (フロント) との通信を許可
		configuration.setAllowedOrigins(List.of("http://localhost:3000"));

		// HTTP メソッドを許可
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		// 送信ヘッダの許可
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		// レスポンスで Authorization ヘッダを露出
		configuration.setExposedHeaders(List.of("Authorization"));

		// Cookie 認証を許可 (JWT 利用時は false でも OK)
		configuration.setAllowCredentials(true); 

		// CORS 設定を適用するパス
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}