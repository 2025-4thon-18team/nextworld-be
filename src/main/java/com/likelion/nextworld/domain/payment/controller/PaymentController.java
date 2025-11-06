package com.likelion.nextworld.domain.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.payment.dto.ChargeRequest;
import com.likelion.nextworld.domain.payment.dto.UseRequest;
import com.likelion.nextworld.domain.payment.dto.VerifyRequest;
import com.likelion.nextworld.domain.payment.service.PaymentService;
import com.likelion.nextworld.domain.user.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping("/charge")
  public ResponseEntity<Void> charge(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody ChargeRequest req) {
    paymentService.charge(user, req);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/verify")
  public ResponseEntity<Boolean> verify(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody VerifyRequest req) {
    boolean ok = paymentService.verify(user, req);
    return ResponseEntity.ok(ok);
  }

  @PostMapping("/use")
  public ResponseEntity<Void> use(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody UseRequest req) {
    paymentService.use(user, req);
    return ResponseEntity.ok().build();
  }
}
