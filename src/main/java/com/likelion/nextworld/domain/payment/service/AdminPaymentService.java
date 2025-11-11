package com.likelion.nextworld.domain.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.payment.dto.PayItemResponse;
import com.likelion.nextworld.domain.payment.entity.Pay;
import com.likelion.nextworld.domain.payment.entity.PayStatus;
import com.likelion.nextworld.domain.payment.repository.PayRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminPaymentService {

  private final PayRepository payRepository;
  private final UserRepository userRepository;
  private final PortOneClient portOneClient;

  @Transactional
  public void approveRefund(Long payId) {
    Pay pay =
        payRepository
            .findById(payId)
            .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다."));

    if (pay.getStatus() != PayStatus.REFUND_REQUESTED) {
      throw new IllegalStateException("환불 요청 상태가 아닙니다.");
    }

    // PortOne 환불 요청
    portOneClient.refundPayment(pay.getImpUid(), pay.getAmount().intValue(), "관리자 승인 환불");

    // 결제 상태 변경
    pay.setStatus(PayStatus.REFUNDED);
    payRepository.save(pay);

    // 사용자 포인트 차감
    User payer = pay.getPayer();
    payer.decreasePoints(pay.getAmount());
  }

  @Transactional(readOnly = true)
  public List<PayItemResponse> getRefundRequests() {
    return payRepository.findByStatus(PayStatus.REFUND_REQUESTED).stream()
        .map(
            p ->
                PayItemResponse.builder()
                    .payId(p.getPayId())
                    .amount(p.getAmount())
                    .type(p.getType())
                    .status(p.getStatus())
                    .impUid(p.getImpUid())
                    .createdAt(p.getCreatedAt())
                    .build())
        .toList();
  }
}
