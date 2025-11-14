package com.likelion.nextworld.domain.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.payment.dto.*;
import com.likelion.nextworld.domain.payment.service.PaymentService;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.response.BaseResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  // 포인트 충전
  @PostMapping("/charge")
  public ResponseEntity<BaseResponse<Void>> charge(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody ChargeRequest req) {

    paymentService.charge(user, req);

    return ResponseEntity.ok(BaseResponse.success("포인트 충전에 성공했습니다.", null));
  }

  // 결제 검증
  @PostMapping("/verify")
  public ResponseEntity<BaseResponse<Boolean>> verify(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody VerifyRequest req) {

    boolean ok = paymentService.verify(user, req);

    return ResponseEntity.ok(BaseResponse.success("결제 검증 결과입니다.", ok));
  }

  // 포인트 사용
  @PostMapping("/use")
  public ResponseEntity<BaseResponse<Void>> use(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody UseRequest req) {

    paymentService.use(user, req);

    return ResponseEntity.ok(BaseResponse.success("포인트 사용이 완료되었습니다.", null));
  }

  // 환불 요청
  @PostMapping("/refund")
  public ResponseEntity<BaseResponse<Void>> requestRefund(
      @AuthenticationPrincipal UserPrincipal user, @RequestBody RefundRequest request) {

    paymentService.requestRefund(request, user.getId());

    return ResponseEntity.ok(BaseResponse.success("환불 요청이 접수되었습니다. 관리자의 승인을 기다려주세요.", null));
  }

  // 충전 옵션 조회
  @GetMapping("/options")
  public ResponseEntity<BaseResponse<ChargeOptionsResponse>> getChargeOptions(
      @AuthenticationPrincipal UserPrincipal user) {

    ChargeOptionsResponse response = paymentService.getChargeOptions(user);

    return ResponseEntity.ok(BaseResponse.success("충전 옵션 조회 성공", response));
  }

  // 충전 내역 조회
  @GetMapping("/history/charge")
  public ResponseEntity<BaseResponse<List<PaymentHistoryResponse>>> getChargeHistory(
      @AuthenticationPrincipal UserPrincipal user) {

    List<PaymentHistoryResponse> response = paymentService.getChargeHistory(user);

    return ResponseEntity.ok(BaseResponse.success("충전 내역 조회 성공", response));
  }

  // 사용 내역 조회
  @GetMapping("/history/use")
  public ResponseEntity<BaseResponse<List<PaymentHistoryResponse>>> getUseHistory(
      @AuthenticationPrincipal UserPrincipal user) {

    List<PaymentHistoryResponse> response = paymentService.getUseHistory(user);

    return ResponseEntity.ok(BaseResponse.success("사용 내역 조회 성공", response));
  }

  // 구매한 Post 리스트
  @GetMapping("/purchases/posts")
  public ResponseEntity<BaseResponse<List<PurchasedWorkResponse>>> getPurchasedPosts(
      @AuthenticationPrincipal UserPrincipal principal) {

    List<PurchasedWorkResponse> list = paymentService.getPurchasedPosts(principal);
    return ResponseEntity.ok(BaseResponse.success("구매한 Post 목록 조회 완료", list));
  }

  // 구매한 Work 리스트
  @GetMapping("/purchases/works")
  public ResponseEntity<BaseResponse<List<PurchasedWorkResponse>>> getPurchasedWorks(
      @AuthenticationPrincipal UserPrincipal principal) {

    List<PurchasedWorkResponse> list = paymentService.getPurchasedWorks(principal);
    return ResponseEntity.ok(BaseResponse.success("구매한 Work 목록 조회 완료", list));
  }

  // 전체 구매 리스트
  @GetMapping("/purchases/all")
  public ResponseEntity<BaseResponse<List<PurchasedWorkResponse>>> getAllPurchases(
      @AuthenticationPrincipal UserPrincipal principal) {

    List<PurchasedWorkResponse> list = paymentService.getAllPurchases(principal);
    return ResponseEntity.ok(BaseResponse.success("전체 구매 목록 조회 완료", list));
  }
}
