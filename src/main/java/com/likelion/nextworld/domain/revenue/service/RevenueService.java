package com.likelion.nextworld.domain.revenue.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.payment.entity.Pay;
import com.likelion.nextworld.domain.payment.repository.PayRepository;
import com.likelion.nextworld.domain.post.entity.DerivativeWork;
import com.likelion.nextworld.domain.post.repository.DerivativeWorkRepository;
import com.likelion.nextworld.domain.revenue.entity.RevenueShare;
import com.likelion.nextworld.domain.revenue.repository.RevenueShareRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RevenueService {

  private final PayRepository payRepository;
  private final RevenueShareRepository revenueShareRepository;
  private final DerivativeWorkRepository derivativeWorkRepository;
  private final UserRepository userRepository;

  /** 수익 분배: 작가 40%, 2차 창작자 30%, 플랫폼 관리자 30% */
  @Transactional
  public void distribute(Long payId, Long derivativeWorkId) {

    Pay pay =
        payRepository.findById(payId).orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));

    // 2차 창작물 정보
    DerivativeWork work =
        derivativeWorkRepository
            .findById(derivativeWorkId)
            .orElseThrow(() -> new IllegalArgumentException("2차 창작물 없음"));

    // 1차 창작자
    User originalAuthor = work.getAuthor();

    // 2차 창작자
    User derivativeAuthor = work.getDAuthor();

    // 플랫폼 관리자 (id=1)
    User platformAdmin =
        userRepository
            .findById(1L)
            .orElseThrow(() -> new IllegalArgumentException("플랫폼 관리자 계정 없음"));

    long amount = pay.getAmount();
    long authorShare = amount * 40 / 100;
    long derivativeShare = amount * 30 / 100;
    long platformShare = amount * 30 / 100;

    // ---- RevenueShare 저장 ----
    revenueShareRepository.save(
        RevenueShare.builder().pay(pay).author(originalAuthor).shareAmount(authorShare).build());

    revenueShareRepository.save(
        RevenueShare.builder()
            .pay(pay)
            .author(derivativeAuthor)
            .shareAmount(derivativeShare)
            .build());

    revenueShareRepository.save(
        RevenueShare.builder().pay(pay).author(platformAdmin).shareAmount(platformShare).build());

    originalAuthor.setTotalEarned(originalAuthor.getTotalEarned() + authorShare);
    derivativeAuthor.setTotalEarned(derivativeAuthor.getTotalEarned() + derivativeShare);
    platformAdmin.setTotalEarned(platformAdmin.getTotalEarned() + platformShare);
  }
}
