package com.likelion.nextworld.global.config;

import com.likelion.nextworld.domain.user.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final CorsConfigurationSource corsConfigurationSource;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter)
      throws Exception {

    http.cors(cors -> cors.configurationSource(corsConfigurationSource))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth
                    // ✅ Swagger 허용
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**")
                    .permitAll()
                    .requestMatchers(
                        "/",
                        "/api/auth/**",
                        "/api/search",
                        "/api/feed/**",
                        "/api/posts/{post_id}/comments",
                        "/api/posts",
                        "/api/posts/{id}")
                    .permitAll()
                    // ✅ 나머지는 JWT 인증 필요
                    .anyRequest()
                    .authenticated())
        // ✅ JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable());

    return http.build();
  }
}
