package com.likelion.nextworld.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "📚 차세계 API 명세서",
            description =
                """
        <p>작가 스핀오프 플랫폼 <strong>차세계 次世界</strong>는<br>
        작가의 세계관을 바탕으로 독자들이 직접 글/그림을 창작하고 공유하는 참여형 커뮤니티 플랫폼입니다.</p>
        """,
            contact =
                @Contact(name = "차세계", url = "https://nxtworld.store", email = "1030n@naver.com")),
    security = @SecurityRequirement(name = "Authorization"),
    servers = {
      @Server(url = "https://api.nxtworld.store", description = "🚀 운영 서버"),
      @Server(url = "http://localhost:8080", description = "🛠️ 로컬 서버")
    })
@SecurityScheme(
    name = "Authorization",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT")
public class SwaggerConfig {

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("Swagger API")
        .pathsToMatch("/api/**", "/swagger-ui/**", "/v3/api-docs/**")
        .build();
  }
}
