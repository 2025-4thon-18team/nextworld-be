package com.likelion.nextworld.domain.revenue.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.payment.entity.Pay;
import com.likelion.nextworld.domain.payment.repository.PayRepository;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.Work;
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

  /** 수익 분배: (원작자 40%, 2차 창작자 30%, 플랫폼 30%) */
  @Transactional
  public void distribute(Long payId, Long postId) {

    Pay pay =
        payRepository.findById(payId).orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));

    Post post =
        postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("포스트 없음"));

    Work work = post.getWork();
    Work original = work.getParentWork();

    User platformAdmin =
        userRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("플랫폼 관리자 없음"));

    long amount = pay.getAmount();

    // 무료 작품 (분배 X)
    if (Boolean.FALSE.equals(work.getAllowDerivative())) return;

    // 1차 창작물인 경우 (원작자 70, 플랫폼 30)
    if (original == null) {
      long authorShare = amount * 70 / 100;
      long platformShare = amount * 30 / 100;

      saveRevenueShare(pay, post, work.getAuthor(), authorShare, null, null);

      saveRevenueShare(pay, post, null, null, null, platformShare);
      return;
    }

    // 2차 창작물인 경우 (원작/창작자/플랫폼 40:30:30)
    long originalShare = amount * 40 / 100;
    long derivativeShare = amount * 30 / 100;
    long platformShare = amount * 30 / 100;

    saveRevenueShare(pay, post, original.getAuthor(), originalShare, null, null);

    saveRevenueShare(pay, post, work.getAuthor(), null, derivativeShare, null);

    saveRevenueShare(pay, post, null, null, null, platformShare);
  }

  // RevenueShare 저장 (3가지 필드 중 필요한 것만 채움)
  private void saveRevenueShare(
      Pay pay,
      Post post,
      User author,
      Long originalAmount,
      Long derivativeAmount,
      Long platformAmount) {
    RevenueShare share =
        RevenueShare.builder()
            .pay(pay)
            .post(post)
            .originalAuthor(originalAmount != null ? author : null)
            .derivativeAuthor(derivativeAmount != null ? author : null)
            .platformUser(platformAmount != null ? author : null)
            .originalAuthorAmount(originalAmount)
            .derivativeAuthorAmount(derivativeAmount)
            .platformAmount(platformAmount)
            .build();

    revenueShareRepository.save(share);
  }

  /** 대시보드 데이터 조회 */
  @Transactional(readOnly = true)
  public RevenueDashboardResponse getRevenueDashboard(Long userId) {
    User author =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    long totalSalesCount = revenueShareRepository.countByDerivativeAuthorOrOriginalAuthor(author);

    long totalEarned = revenueShareRepository.sumAmountByDerivativeAuthorOrOriginalAuthor(author);

    return RevenueDashboardResponse.builder()
        .totalSalesCount(totalSalesCount)
        .totalRevenue(totalEarned)
        .build();
  }

  /** 판매 이력 조회 */
  @Transactional(readOnly = true)
  public List<RevenueSaleItemResponse> getSalesHistory(Long userId) {

    User author =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    List<RevenueShare> shares =
        revenueShareRepository.findSalesByDerivativeAuthorOrOriginalAuthor(author);

    return shares.stream()
        .map(
            share -> {
              Post post = share.getPost();
              Pay pay = share.getPay();
              User buyer = pay.getPayer();

              long myShare = calculateAuthorShare(share, author);

              return RevenueSaleItemResponse.builder()
                  .postTitle(post.getTitle())
                  .buyerNickname(buyer.getNickname())
                  .amount(myShare)
                  .date(pay.getCreatedAt())
                  .build();
            })
        .toList();
  }

  /** 원작/2차 여부에 따라 내 몫 계산 */
  private long calculateAuthorShare(RevenueShare share, User author) {
    long amount = 0L;

    if (author.equals(share.getOriginalAuthor())) {
      amount += (share.getOriginalAuthorAmount() != null ? share.getOriginalAuthorAmount() : 0L);
    }

    if (author.equals(share.getDerivativeAuthor())) {
      amount +=
          (share.getDerivativeAuthorAmount() != null ? share.getDerivativeAuthorAmount() : 0L);
    }

    return amount;
  }

  /** 정산 처리 */
  @Transactional
  public RevenueSettleResponse settleRevenue(Long userId) {
    User author =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    // 총 수익
    long totalEarned = revenueShareRepository.sumAmountByDerivativeAuthorOrOriginalAuthor(author);

    // 지금까지 정산된 금액 (History 기준)
    long totalSettled =
        revenueSettlementHistoryRepository.findByAuthorOrderBySettledAtDesc(author).stream()
            .mapToLong(RevenueSettlementHistory::getSettledAmount)
            .sum();

    long unsettled = totalEarned - totalSettled;
    if (unsettled <= 0) {
      return new RevenueSettleResponse(0L, author.getPointsBalance(), 0L);
    }

    long prevBalance = author.getPointsBalance();
    long newBalance = prevBalance + unsettled;
    author.setPointsBalance(newBalance);

    RevenueSettlementHistory history =
        RevenueSettlementHistory.builder()
            .author(author)
            .settledAmount(unsettled)
            .previousBalance(prevBalance)
            .newBalance(newBalance)
            .build();

    revenueSettlementHistoryRepository.save(history);

    return new RevenueSettleResponse(
        unsettled, newBalance, totalEarned - (totalSettled + unsettled));
  }

  /** 정산 내역 조회 */
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
