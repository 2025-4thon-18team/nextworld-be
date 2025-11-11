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

  /** 수익 분배: 원작자 40%, 2차 창작자 30%, 플랫폼 30% */
  @Transactional
  public void distribute(Long payId, Long postId) {

    Pay pay =
        payRepository.findById(payId).orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));
    Post post =
        postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("포스트 없음"));
    Work work = post.getParentWork(); // ✅ 이건 있음 (Post → Work)

    User platformAdmin =
        userRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("플랫폼 관리자 없음"));
    long amount = pay.getAmount();

    // 무료 작품은 분배 없음
    if (Boolean.FALSE.equals(work.getIsPaid())) return;

    // 1차 창작물(Post가 아닌, 원작 Work만 존재) → 5:5
    // Post는 항상 2차니까, 이 경우는 else로 처리할 수도 있음
    if (post.getParentWork() == null) {
      long authorShare = amount * 50 / 100;
      long platformShare = amount * 50 / 100;
      saveRevenueShares(pay, work.getAuthor(), authorShare);
      saveRevenueShares(pay, platformAdmin, platformShare);
      return;
    }

    // 2차 창작물 → 4:3:3 (원작자, 2차 작가, 플랫폼)
    Work original = post.getParentWork();
    if (Boolean.TRUE.equals(original.getAllowDerivative())
        && Boolean.TRUE.equals(original.getAllowDerivativeProfit())) {

      long originalShare = amount * 40 / 100;
      long derivativeShare = amount * 30 / 100;
      long platformShare = amount * 30 / 100;

      saveRevenueShares(pay, original.getAuthor(), originalShare);
      saveRevenueShares(pay, work.getAuthor(), derivativeShare);
      saveRevenueShares(pay, platformAdmin, platformShare);
    }
  }

  /** 내부 공통 메서드 - 분배 저장 및 작가 수익 누적 */
  private void saveRevenueShares(Pay pay, User author, long shareAmount) {
    revenueShareRepository.save(
        RevenueShare.builder().pay(pay).author(author).shareAmount(shareAmount).build());
    author.setTotalEarned(author.getTotalEarned() + shareAmount);
  }

  @Transactional(readOnly = true)
  public RevenueDashboardResponse getRevenueDashboard(Long userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    long salesCount = revenueShareRepository.countByAuthor(user);
    long totalRevenue = revenueShareRepository.sumShareAmountByAuthor(user);

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

    List<RevenueShare> sales = revenueShareRepository.findSalesByAuthor(seller);

    return sales.stream()
        .map(
            r ->
                RevenueSaleItemResponse.builder()
                    .postTitle(r.getPay().getPost().getTitle())
                    .buyerNickname(r.getPay().getPayer().getNickname())
                    .amount(r.getShareAmount())
                    .date(r.getPay().getCreatedAt())
                    .build())
        .toList();
  }

  /** 정산 처리 + 정산 내역 저장 */
  @Transactional
  public RevenueSettleResponse settleRevenue(Long userId) {
    User author =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    long unsettledAmount = revenueShareRepository.findUnsettledAmountByAuthor(author);
    if (unsettledAmount == 0) {
      return new RevenueSettleResponse(0L, 0L, author.getPointsBalance());
    }

    long prevBalance = author.getPointsBalance();

    author.setPointsBalance(prevBalance + unsettledAmount);

    List<RevenueShare> unsettledShares = revenueShareRepository.findByAuthorAndSettledFalse(author);
    unsettledShares.forEach(RevenueShare::markAsSettled);
    revenueShareRepository.saveAll(unsettledShares);

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
