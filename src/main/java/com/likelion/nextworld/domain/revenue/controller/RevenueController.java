package com.likelion.nextworld.domain.revenue.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.revenue.dto.RevenueDashboardResponse;
import com.likelion.nextworld.domain.revenue.dto.RevenueSaleItemResponse;
import com.likelion.nextworld.domain.revenue.dto.RevenueSettleHistoryResponse;
import com.likelion.nextworld.domain.revenue.dto.RevenueSettleResponse;
import com.likelion.nextworld.domain.revenue.service.RevenueService;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.response.BaseResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/revenue")
@RequiredArgsConstructor
public class RevenueController {

  private final RevenueService revenueService;

  @PostMapping("/distribute")
  public ResponseEntity<BaseResponse<Void>> distribute(@RequestBody DistributeRequest req) {
    revenueService.distribute(req.getPayId(), req.getDerivativeWorkId());
    return ResponseEntity.ok(BaseResponse.success("수익 분배가 완료되었습니다.", null));
  }

  @Getter
  static class DistributeRequest {

    private Long payId;
    private Long derivativeWorkId;
  }

  @GetMapping("/dashboard")
  public ResponseEntity<BaseResponse<RevenueDashboardResponse>> getDashboard(
      @AuthenticationPrincipal UserPrincipal user) {
    RevenueDashboardResponse response = revenueService.getRevenueDashboard(user.getId());
    return ResponseEntity.ok(BaseResponse.success("대시보드 조회 성공", response));
  }

  @GetMapping("/sales")
  public ResponseEntity<BaseResponse<List<RevenueSaleItemResponse>>> getSalesHistory(
      @AuthenticationPrincipal UserPrincipal user) {
    List<RevenueSaleItemResponse> response = revenueService.getSalesHistory(user.getId());
    return ResponseEntity.ok(BaseResponse.success("매출 내역 조회 성공", response));
  }

  @PostMapping("/settle")
  public ResponseEntity<BaseResponse<RevenueSettleResponse>> settleRevenue(
      @AuthenticationPrincipal UserPrincipal user) {
    RevenueSettleResponse response = revenueService.settleRevenue(user.getId());
    return ResponseEntity.ok(BaseResponse.success("정산이 완료되었습니다.", response));
  }

  @GetMapping("/settle/history")
  public ResponseEntity<BaseResponse<List<RevenueSettleHistoryResponse>>> getSettleHistory(
      @AuthenticationPrincipal UserPrincipal user) {
    List<RevenueSettleHistoryResponse> response = revenueService.getSettleHistory(user.getId());
    return ResponseEntity.ok(BaseResponse.success("정산 내역 조회 성공", response));
  }
}
