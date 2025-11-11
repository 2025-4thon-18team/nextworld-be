package com.likelion.nextworld.global.ai;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OpenAiClient {

  @Value("${openai.api.key}")
  private String apiKey;

  private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

  public String analyze(String prompt) {
    RestTemplate restTemplate = new RestTemplate();

    Map<String, Object> body =
        Map.of(
            "model",
            "gpt-4o-mini",
            "messages",
            new Object[] {Map.of("role", "user", "content", prompt)},
            "max_tokens",
            50);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(apiKey);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, request, Map.class);

    // 결과 텍스트 꺼내기
    try {
      return ((Map<String, Object>)
              ((Map<String, Object>) ((java.util.List<?>) response.getBody().get("choices")).get(0))
                  .get("message"))
          .get("content")
          .toString();
    } catch (Exception e) {
      return "ERROR";
    }
  }
}
