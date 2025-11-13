package com.likelion.nextworld.domain.payment.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.payment.dto.*;
import com.likelion.nextworld.domain.payment.entity.Pay;
import com.likelion.nextworld.domain.payment.entity.PayStatus;
import com.likelion.nextworld.domain.payment.entity.TransactionType;
import com.likelion.nextworld.domain.payment.repository.PayRepository;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.post.repository.WorkRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
  private final PayRepository payRepository;
  private final UserRepository userRepository;
  private final WorkRepository workRepository;
  private final PostRepository postRepository;
  private final PortOneClient portOneClient;

  @Transactional
  public void charge(UserPrincipal principal, ChargeRequest req) {
    User payer = getUser(principal.getId());

    payRepository
        .findByImpUid(req.getImpUid())
        .ifPresent(
            p -> {
              throw new IllegalStateException("이미 처리된 결제입니다.");
            });

    Pay pay = Pay.createCharge(payer, req.getAmount(), req.getImpUid());

    payRepository.save(pay);
  }

  @Transactional
  public boolean verify(UserPrincipal principal, VerifyRequest req) {
    User payer = getUser(principal.getId());

    Pay pay =
        payRepository
            .findByImpUid(req.getImpUid())
            .orElseThrow(() -> new IllegalArgumentException("해당 impUid로 생성된 결제가 없습니다."));

    PortOneClient.PaymentLookup lookup = portOneClient.lookup(req.getImpUid());
    if (!"paid".equalsIgnoreCase(lookup.getResponse().getStatus()))
      throw new IllegalStateException("결제가 승인되지 않았습니다.");

    Long paidAmount = lookup.getResponse().getAmount();
    if (!paidAmount.equals(pay.getAmount())) throw new IllegalStateException("금액 불일치");

    payer.setPointsBalance(payer.getPointsBalance() + paidAmount);
    pay.setStatus(PayStatus.COMPLETED);

    return true;
  }

  @Transactional
  public void use(UserPrincipal principal, UseRequest req) {
    User payer = getUser(principal.getId());
    if (payer.getPointsBalance() < req.getAmount()) throw new IllegalStateException("포인트가 부족합니다.");

    payer.setPointsBalance(payer.getPointsBalance() - req.getAmount());

    Pay pay = Pay.createUse(payer, req.getAmount(), req.getPostId(), req.getDerivativeWorkId());

    payRepository.save(pay);
  }

  @Transactional
  public void requestRefund(RefundRequest request, Long userId) {
    Pay pay =
        payRepository
            .findByImpUid(request.getImpUid())
            .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다."));

    if (pay.getStatus() != PayStatus.COMPLETED)
      throw new IllegalStateException("환불 요청은 결제 완료 상태에서만 가능합니다.");

    pay.setStatus(PayStatus.REFUND_REQUESTED);
    payRepository.save(pay);
  }

  public List<PayItemResponse> myPayList(UserPrincipal principal) {
    User me = getUser(principal.getId());
    return payRepository.findByPayerOrderByCreatedAtDesc(me).stream()
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

  private User getUser(Long id) {
    return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
  }

  public ChargeOptionsResponse getChargeOptions(UserPrincipal user) {
    Long currentPoints =
        userRepository
            .findById(user.getId())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."))
            .getPointsBalance();

    List<ChargeOptionsResponse.ChargeOption> options =
        List.of(
            new ChargeOptionsResponse.ChargeOption(1000L, 1000L, currentPoints + 1000L),
            new ChargeOptionsResponse.ChargeOption(3000L, 3000L, currentPoints + 3000L),
            new ChargeOptionsResponse.ChargeOption(5000L, 5000L, currentPoints + 5000L),
            new ChargeOptionsResponse.ChargeOption(10000L, 10000L, currentPoints + 10000L));

    List<String> paymentMethods = List.of("네이버페이", "카카오페이", "카드결제", "계좌이체");

    return new ChargeOptionsResponse(currentPoints, options, paymentMethods);
  }

  @Transactional(readOnly = true)
  public List<PaymentHistoryResponse> getChargeHistory(UserPrincipal principal) {
    User user =
        userRepository
            .findById(principal.getId())
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    List<Pay> payments =
        payRepository.findByPayerAndTypeOrderByCreatedAtDesc(user, TransactionType.CHARGE);

    return payments.stream()
        .map(
            p ->
                PaymentHistoryResponse.builder()
                    .title("포인트 충전")
                    .opponentName(null)
                    .amount(p.getAmount())
                    .date(p.getCreatedAt())
                    .build())
        .toList();
  }

  @Transactional(readOnly = true)
  public List<PaymentHistoryResponse> getUseHistory(UserPrincipal principal) {
    User user =
        userRepository
            .findById(principal.getId())
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    List<Pay> payments =
        payRepository.findByPayerAndTypeOrderByCreatedAtDesc(user, TransactionType.USE);

    return payments.stream()
        .map(
            p ->
                PaymentHistoryResponse.builder()
                    .title("포인트 사용")
                    .opponentName(null)
                    .amount(-p.getAmount())
                    .date(p.getCreatedAt())
                    .build())
        .toList();
  }

  @Transactional(readOnly = true)
  public List<PurchasedWorkResponse> getPurchasedPosts(UserPrincipal principal) {
    User user = getUser(principal.getId());

    List<Pay> purchases =
        payRepository.findByPayerAndTypeAndPostIdIsNotNullOrderByCreatedAtDesc(
            user, TransactionType.USE);

    return purchases.stream()
        .map(
            p -> {
              Post post = postRepository.findById(p.getPostId()).orElse(null);

              return PurchasedWorkResponse.builder()
                  .postId(p.getPostId())
                  .workId(post != null && post.getWork() != null ? post.getWork().getId() : null)
                  .title(post != null ? post.getTitle() : null)
                  .amount(p.getAmount())
                  .purchasedAt(p.getCreatedAt())
                  .build();
            })
        .toList();
  }

  @Transactional(readOnly = true)
  public List<PurchasedWorkResponse> getPurchasedWorks(UserPrincipal principal) {
    User user = getUser(principal.getId());

    List<Pay> purchases =
        payRepository.findByPayerAndTypeAndWorkIdIsNotNullOrderByCreatedAtDesc(
            user, TransactionType.USE);

    return purchases.stream()
        .map(
            p -> {
              Work work = workRepository.findById(p.getWorkId()).orElse(null);

              return PurchasedWorkResponse.builder()
                  .postId(null)
                  .workId(p.getWorkId())
                  .title(work != null ? work.getTitle() : null)
                  .coverImageUrl(work != null ? work.getCoverImageUrl() : null)
                  .workType(work != null ? work.getWorkType().name() : null)
                  .parentWorkId(
                      work != null && work.getParentWork() != null
                          ? work.getParentWork().getId()
                          : null)
                  .amount(p.getAmount())
                  .purchasedAt(p.getCreatedAt())
                  .build();
            })
        .toList();
  }

  @Transactional(readOnly = true)
  public List<PurchasedWorkResponse> getAllPurchases(UserPrincipal principal) {
    List<PurchasedWorkResponse> posts = getPurchasedPosts(principal);
    List<PurchasedWorkResponse> works = getPurchasedWorks(principal);

    List<PurchasedWorkResponse> all = new ArrayList<>();
    all.addAll(posts);
    all.addAll(works);

    all.sort(Comparator.comparing(PurchasedWorkResponse::getPurchasedAt).reversed());

    return all;
  }
}
