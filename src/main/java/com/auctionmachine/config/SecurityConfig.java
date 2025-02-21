package com.auctionmachine.config;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // (1) CSRFを無効化 (API利用の場合)
            .csrf(csrf -> csrf.disable())
            
            // (2) CORSを有効化
            .cors(cors -> { 
                // 必要ならここで `corsConfigurationSource()` を指定
            })

            // 認証のパターン
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() 
                .anyRequest().authenticated()
            );
        
        return http.build();
    }

    // (オプション) ここでCORS用設定をBeanにして返す
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Reactアプリが http://localhost:3000 ならそれを許可
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        
        // 使用するHTTPメソッドを許可
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        
        // 送信されるヘッダを許可
        // Authorization ヘッダが必要なので、ここに含める
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        
        // (必要に応じて) レスポンスヘッダのExposeなどを設定
        configuration.setExposedHeaders(List.of("Authorization"));

        //configuration.setAllowCredentials(true);

        // このCORS設定を適用するパスパターン
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}