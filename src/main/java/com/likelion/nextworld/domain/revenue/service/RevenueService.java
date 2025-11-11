package com.likelion.nextworld.domain.revenue.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.payment.entity.Pay;
import com.likelion.nextworld.domain.payment.repository.PayRepository;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.revenue.dto.RevenueDashboardResponse;
import com.likelion.nextworld.domain.revenue.dto.RevenueSaleItemResponse;
import com.likelion.nextworld.domain.revenue.dto.RevenueSettleHistoryResponse;
import com.likelion.nextworld.domain.revenue.dto.RevenueSettleResponse;
import com.likelion.nextworld.domain.revenue.entity.RevenueSettlementHistory;
import com.likelion.nextworld.domain.revenue.entity.RevenueShare;
import com.likelion.nextworld.domain.revenue.repository.RevenueSettlementHistoryRepository;
import com.likelion.nextworld.domain.revenue.repository.RevenueShareRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RevenueService {

  private final PayRepository payRepository;
  private final RevenueShareRepository revenueShareRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final RevenueSettlementHistoryRepository revenueSettlementHistoryRepository;

  /** 수익 분배: 원작자 40%, 2차 창작자 30%, 플랫폼 30% */
  @Transactional
  public void distribute(Long payId, Long postId) {

    Pay pay =
        payRepository.findById(payId).orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));

    Post post =
        postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("포스트 없음"));

    User derivativeAuthor = post.getAuthor();
    User originalAuthor = null;

    // 원작자가 있는 경우 (2차 창작인 경우)
    if (post.getParentWork() != null) {
      originalAuthor = post.getParentWork().getAuthor();
    }

    // 플랫폼 관리자 (id=1)
    User platformAdmin =
        userRepository
            .findById(1L)
            .orElseThrow(() -> new IllegalArgumentException("플랫폼 관리자 계정 없음"));

    long amount = pay.getAmount();
    long authorShare = originalAuthor != null ? amount * 40 / 100 : 0;
    long derivativeShare = amount * 30 / 100;
    long platformShare = amount * 30 / 100;

    // 수익 분배 저장 (각자 분배 포인트와 정산 금액은 동일하게 설정)
    if (originalAuthor != null) {
      revenueShareRepository.save(
          RevenueShare.builder()
              .pay(pay)
              .post(post)
              .originalAuthor(originalAuthor)
              .derivativeAuthor(derivativeAuthor)
              .admin(platformAdmin)
              .shareEach(authorShare)
              .valueEach(authorShare)
              .build());
    }

    revenueShareRepository.save(
        RevenueShare.builder()
            .pay(pay)
            .post(post)
            .originalAuthor(originalAuthor)
            .derivativeAuthor(derivativeAuthor)
            .admin(platformAdmin)
            .shareEach(derivativeShare)
            .valueEach(derivativeShare)
            .build());

    revenueShareRepository.save(
        RevenueShare.builder()
            .pay(pay)
            .post(post)
            .originalAuthor(originalAuthor)
            .derivativeAuthor(derivativeAuthor)
            .admin(platformAdmin)
            .shareEach(platformShare)
            .valueEach(platformShare)
            .build());

    if (originalAuthor != null) {
      originalAuthor.setTotalEarned(originalAuthor.getTotalEarned() + authorShare);
    }
    derivativeAuthor.setTotalEarned(derivativeAuthor.getTotalEarned() + derivativeShare);
    platformAdmin.setTotalEarned(platformAdmin.getTotalEarned() + platformShare);
  }

  @Transactional(readOnly = true)
  public RevenueDashboardResponse getRevenueDashboard(Long userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    // derivativeAuthor 또는 originalAuthor로 수익 조회
    long salesCount = revenueShareRepository.countByDerivativeAuthorOrOriginalAuthor(user);
    long totalRevenue = revenueShareRepository.sumShareEachByDerivativeAuthorOrOriginalAuthor(user);

    long originalAuthorFee = totalRevenue * 40 / 100;
    long platformFee = totalRevenue * 30 / 100;

    long netIncome = totalRevenue - originalAuthorFee - platformFee;

    return RevenueDashboardResponse.builder()
        .totalSalesCount(salesCount)
        .totalRevenue(totalRevenue)
        .originalAuthorFee(originalAuthorFee)
        .platformFee(platformFee)
        .netIncome(netIncome)
        .build();
  }

  @Transactional(readOnly = true)
  public List<RevenueSaleItemResponse> getSalesHistory(Long userId) {
    User seller =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    List<RevenueShare> sales =
        revenueShareRepository.findSalesByDerivativeAuthorOrOriginalAuthor(seller);

    return sales.stream()
        .map(
            r ->
                RevenueSaleItemResponse.builder()
                    .postTitle(r.getPost().getTitle())
                    .buyerNickname(r.getPay().getPayer().getNickname())
                    .amount(r.getShareEach())
                    .date(r.getPay().getCreatedAt())
                    .build())
        .toList();
  }

  /** 정산 처리 + 정산 내역 저장 */
  @Transactional
  public RevenueSettleResponse settleRevenue(Long userId) {
    User author =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    // TODO: RevenueShare의 settled 필드가 제거되었으므로, 정산 로직 재구현 필요
    // 현재는 임시로 0 반환
    long unsettledAmount = 0L;
    if (unsettledAmount == 0) {
      return new RevenueSettleResponse(0L, 0L, author.getPointsBalance());
    }

    long prevBalance = author.getPointsBalance();
    author.setPointsBalance(prevBalance + unsettledAmount);
    userRepository.save(author);

    RevenueSettlementHistory history =
        RevenueSettlementHistory.builder()
            .author(author)
            .settledAmount(unsettledAmount)
            .previousBalance(prevBalance)
            .newBalance(author.getPointsBalance())
            .build();
    revenueSettlementHistoryRepository.save(history);

    long remainingUnsettled = 0L;

    return RevenueSettleResponse.builder()
        .totalSettledAmount(unsettledAmount)
        .remainingUnsettled(remainingUnsettled)
        .newPointsBalance(author.getPointsBalance())
        .build();
  }

  @Transactional(readOnly = true)
  public List<RevenueSettleHistoryResponse> getSettleHistory(Long userId) {
    User author =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    return revenueSettlementHistoryRepository.findByAuthorOrderBySettledAtDesc(author).stream()
        .map(
            h ->
                RevenueSettleHistoryResponse.builder()
                    .settledAmount(h.getSettledAmount())
                    .previousBalance(h.getPreviousBalance())
                    .newBalance(h.getNewBalance())
                    .settledAt(h.getSettledAt())
                    .build())
        .toList();
  }
}
