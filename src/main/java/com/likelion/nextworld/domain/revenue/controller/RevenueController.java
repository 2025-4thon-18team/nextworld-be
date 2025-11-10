package com.likelion.nextworld.domain.revenue.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.revenue.dto.RevenueDashboardResponse;
import com.likelion.nextworld.domain.revenue.dto.RevenueSaleItemResponse;
import com.likelion.nextworld.domain.revenue.dto.RevenueSettleHistoryResponse;
import com.likelion.nextworld.domain.revenue.dto.RevenueSettleResponse;
import com.likelion.nextworld.domain.revenue.service.RevenueService;
import com.likelion.nextworld.domain.user.security.UserPrincipal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/revenue")
@RequiredArgsConstructor
public class RevenueController {

  private final RevenueService revenueService;

  @PostMapping("/distribute")
  public ResponseEntity<Void> distribute(@RequestBody DistributeRequest req) {
    revenueService.distribute(req.getPayId(), req.getDerivativeWorkId());
    return ResponseEntity.ok().build();
  }

  @Getter
  static class DistributeRequest {
    private Long payId;
    private Long derivativeWorkId;
  }

  @GetMapping("/dashboard")
  public ResponseEntity<RevenueDashboardResponse> getDashboard(
      @AuthenticationPrincipal UserPrincipal user) {
    RevenueDashboardResponse response = revenueService.getRevenueDashboard(user.getId());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/sales")
  public ResponseEntity<List<RevenueSaleItemResponse>> getSalesHistory(
      @AuthenticationPrincipal UserPrincipal user) {
    List<RevenueSaleItemResponse> response = revenueService.getSalesHistory(user.getId());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/settle")
  public ResponseEntity<RevenueSettleResponse> settleRevenue(
      @AuthenticationPrincipal UserPrincipal user) {
    RevenueSettleResponse response = revenueService.settleRevenue(user.getId());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/settle/history")
  public ResponseEntity<List<RevenueSettleHistoryResponse>> getSettleHistory(
      @AuthenticationPrincipal UserPrincipal user) {
    List<RevenueSettleHistoryResponse> response = revenueService.getSettleHistory(user.getId());
    return ResponseEntity.ok(response);
  }
}
