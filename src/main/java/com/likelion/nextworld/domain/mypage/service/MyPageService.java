package com.likelion.nextworld.domain.mypage.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.nextworld.domain.payment.dto.PayItemResponse;
import com.likelion.nextworld.domain.payment.dto.PointsResponse;
import com.likelion.nextworld.domain.payment.entity.Pay;
import com.likelion.nextworld.domain.payment.entity.PayStatus;
import com.likelion.nextworld.domain.payment.entity.TransactionType;
import com.likelion.nextworld.domain.payment.repository.PayRepository;
import com.likelion.nextworld.domain.payment.service.PaymentService;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
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
public class MyPageService {
  private final UserRepository userRepository;
  private final PaymentService paymentService;
  private final PayRepository payRepository;
  private final PostRepository postRepository;
  private final WorkRepository workRepository;

  public PointsResponse myPoints(UserPrincipal principal) {
    User me =
        userRepository
            .findById(principal.getId())
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    return new PointsResponse(me.getPointsBalance());
  }

  public List<PayItemResponse> myPayList(UserPrincipal principal) {
    return paymentService.myPayList(principal);
  }

  @Transactional(readOnly = true)
  public List<PostResponseDto> getPurchasedPosts(UserPrincipal principal) {
    User user =
        userRepository
            .findById(principal.getId())
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    // 결제 완료된 Post 조회
    List<Pay> payments =
        payRepository.findByPayerAndTransactionTypeAndPostIsNotNullAndPayStatusOrderByCreatedAtDesc(
            user, TransactionType.USE, PayStatus.COMPLETED);

    // 중복 제거하여 Post 목록 추출
    Set<Long> postIds =
        payments.stream()
            .map(pay -> pay.getPost().getId())
            .collect(Collectors.toSet());

    List<Post> posts = postRepository.findAllById(postIds);

    return posts.stream().map(PostResponseDto::new).toList();
  }

  @Transactional(readOnly = true)
  public List<WorkResponseDto> getPurchasedWorks(UserPrincipal principal) {
    User user =
        userRepository
            .findById(principal.getId())
            .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

    // 결제 완료된 Post 조회
    List<Pay> payments =
        payRepository.findByPayerAndTransactionTypeAndPostIsNotNullAndPayStatusOrderByCreatedAtDesc(
            user, TransactionType.USE, PayStatus.COMPLETED);

    // Post의 work_id와 parent_work_id를 수집
    Set<Long> workIds =
        payments.stream()
            .filter(pay -> pay.getPost() != null)
            .flatMap(
                pay -> {
                  Post post = pay.getPost();
                  return java.util.stream.Stream.of(
                          post.getWork() != null ? post.getWork().getId() : null,
                          post.getParentWork() != null ? post.getParentWork().getId() : null)
                      .filter(java.util.Objects::nonNull);
                })
            .collect(Collectors.toSet());

    List<Work> works = workRepository.findAllById(workIds);

    return works.stream().map(WorkResponseDto::new).toList();
  }
}
