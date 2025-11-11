package com.likelion.nextworld.domain.revenue.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.likelion.nextworld.domain.revenue.dto.DistributeRequest;
import com.likelion.nextworld.domain.revenue.dto.RevenueDashboardResponse;
import com.likelion.nextworld.domain.revenue.dto.RevenueSaleItemResponse;
import com.likelion.nextworld.domain.revenue.dto.RevenueSettleHistoryResponse;
import com.likelion.nextworld.domain.revenue.dto.RevenueSettleResponse;
import com.likelion.nextworld.domain.revenue.service.RevenueService;
import com.likelion.nextworld.domain.user.security.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/revenue")
@RequiredArgsConstructor
@Tag(name = "Revenue", description = "수익 관리 API")
@SecurityRequirement(name = "Authorization")
public class RevenueController {

  private final RevenueService revenueService;

  @Operation(summary = "수익 분배", description = "결제된 작품의 수익을 분배합니다.")
  @PostMapping("/distribute")
  public ResponseEntity<Void> distribute(@RequestBody DistributeRequest req) {
    revenueService.distribute(req.getPayId(), req.getPostId());
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "수익 대시보드 조회", description = "사용자의 수익 대시보드 정보를 조회합니다.")
  @GetMapping("/dashboard")
  public ResponseEntity<RevenueDashboardResponse> getDashboard(
      @AuthenticationPrincipal UserPrincipal user) {
    RevenueDashboardResponse response = revenueService.getRevenueDashboard(user.getId());
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "판매 내역 조회", description = "사용자의 판매 내역을 조회합니다.")
  @GetMapping("/sales")
  public ResponseEntity<List<RevenueSaleItemResponse>> getSalesHistory(
      @AuthenticationPrincipal UserPrincipal user) {
    List<RevenueSaleItemResponse> response = revenueService.getSalesHistory(user.getId());
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "수익 정산 요청", description = "누적된 수익을 정산 요청합니다.")
  @PostMapping("/settle")
  public ResponseEntity<RevenueSettleResponse> settleRevenue(
      @AuthenticationPrincipal UserPrincipal user) {
    RevenueSettleResponse response = revenueService.settleRevenue(user.getId());
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "정산 내역 조회", description = "사용자의 정산 내역을 조회합니다.")
  @GetMapping("/settle/history")
  public ResponseEntity<List<RevenueSettleHistoryResponse>> getSettleHistory(
      @AuthenticationPrincipal UserPrincipal user) {
    List<RevenueSettleHistoryResponse> response = revenueService.getSettleHistory(user.getId());
    return ResponseEntity.ok(response);
  }
}
