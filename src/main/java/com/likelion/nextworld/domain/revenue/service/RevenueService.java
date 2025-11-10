package com.likelion.nextworld.domain.revenue.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.payment.entity.Pay;
import com.likelion.nextworld.domain.payment.repository.PayRepository;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.repository.PostRepository;
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
  private final PostRepository postRepository;
  private final UserRepository userRepository;

  /** 수익 분배: 원작자 40%, 2차 창작자 30%, 플랫폼 30% */
  @Transactional
  public void distribute(Long payId, Long derivativePostId) {

    Pay pay =
        payRepository.findById(payId).orElseThrow(() -> new IllegalArgumentException("결제 내역 없음"));

    Post post =
        postRepository
            .findById(derivativePostId)
            .orElseThrow(() -> new IllegalArgumentException("2차 창작물(Post) 없음"));

    User originalAuthor = post.getParentWork().getAuthor();

    User derivativeAuthor = post.getAuthor();

    // 플랫폼 관리자 (id=1)
    User platformAdmin =
        userRepository
            .findById(1L)
            .orElseThrow(() -> new IllegalArgumentException("플랫폼 관리자 계정 없음"));

    long amount = pay.getAmount();
    long authorShare = amount * 40 / 100;
    long derivativeShare = amount * 30 / 100;
    long platformShare = amount * 30 / 100;

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
