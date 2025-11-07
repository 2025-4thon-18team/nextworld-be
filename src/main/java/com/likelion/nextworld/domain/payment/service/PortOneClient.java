package com.likelion.nextworld.domain.payment.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PortOneClient {

  private final WebClient webClient = WebClient.builder().baseUrl("https://api.iamport.kr").build();

  @Value("${portone.api_key}")
  private String apiKey;

  @Value("${portone.api_secret}")
  private String apiSecret;

  public String getAccessToken() {
    return webClient
        .post()
        .uri("/users/getToken")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(String.format("{\"imp_key\":\"%s\",\"imp_secret\":\"%s\"}", apiKey, apiSecret))
        .retrieve()
        .bodyToMono(TokenResponse.class)
        .block()
        .getResponse()
        .getAccess_token();
  }

  public PaymentLookup lookup(String impUid) {
    String token = getAccessToken();
    return webClient
        .get()
        .uri("/payments/{impUid}", impUid)
        .header("Authorization", token)
        .retrieve()
        .bodyToMono(PaymentLookup.class)
        .block();
  }

  @lombok.Getter
  static class TokenResponse {
    Inner response;

    @lombok.Getter
    static class Inner {
      String access_token;
    }
  }

  @lombok.Getter
  public static class PaymentLookup {
    Inner response;

    @lombok.Getter
    public static class Inner {
      String status; // paid, ready, failed ...
      Long amount;
      String imp_uid;
      String merchant_uid;
    }
  }

  public void refundPayment(String impUid, int amount, String reason) {
    WebClient client =
        WebClient.builder()
            .baseUrl("https://api.iamport.kr")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
            .build();

    Map<String, Object> body =
        Map.of(
            "reason", reason,
            "imp_uid", impUid,
            "amount", amount);

    client
        .post()
        .uri("/payments/cancel")
        .bodyValue(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, res -> res.createException().flatMap(Mono::error))
        .bodyToMono(Void.class)
        .block();
  }
}
