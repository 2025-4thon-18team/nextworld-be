package com.likelion.nextworld.domain.revenue.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.nextworld.domain.revenue.service.RevenueService;

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
}
