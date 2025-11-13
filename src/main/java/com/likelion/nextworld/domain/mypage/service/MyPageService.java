package com.likelion.nextworld.domain.mypage.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.likelion.nextworld.domain.mypage.dto.ProfileUpdateRequest;
import com.likelion.nextworld.domain.payment.dto.PayItemResponse;
import com.likelion.nextworld.domain.payment.dto.PointsResponse;
import com.likelion.nextworld.domain.payment.service.PaymentService;
import com.likelion.nextworld.domain.post.dto.PostResponseDto;
import com.likelion.nextworld.domain.post.dto.WorkResponseDto;
import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.post.repository.WorkRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.exception.UserErrorCode;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.exception.CustomException;
import com.likelion.nextworld.global.service.S3Uploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final UserRepository userRepository;
  private final PaymentService paymentService;
  private final S3Uploader s3Uploader;
  private final PostRepository postRepository;
  private final WorkRepository workRepository;

  private User getCurrentUser(UserPrincipal principal) {
    if (principal == null || principal.getId() == null) {
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }
    return userRepository
        .findById(principal.getId())
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

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

  public void updateProfile(UserPrincipal principal, ProfileUpdateRequest request) {
    User user =
        userRepository
            .findById(principal.getId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    if (request.getName() != null && !request.getName().isBlank()) {
      user.setName(request.getName());
    }

    if (request.getBio() != null) {
      user.setBio(request.getBio());
    }

    if (request.getTwitter() != null) {
      user.setTwitter(request.getTwitter());
    }

    if (request.getContactEmail() != null) {
      user.setContactEmail(request.getContactEmail());
    }

    MultipartFile profileImage = request.getProfileImage();
    if (profileImage != null && !profileImage.isEmpty()) {
      try {
        String imageUrl = s3Uploader.upload(profileImage, "profile");
        user.setProfileImageUrl(imageUrl);
      } catch (IOException e) {
        throw new RuntimeException("프로필 이미지 업로드에 실패했습니다.", e);
      }
    }

    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public List<WorkResponseDto> myWorks(UserPrincipal principal) {
    User user = getCurrentUser(principal);

    List<Work> works = workRepository.findAllByAuthor(user);

    return works.stream().map(WorkResponseDto::new).toList();
  }

  @Transactional(readOnly = true)
  public List<PostResponseDto> myPosts(UserPrincipal principal) {
    User user = getCurrentUser(principal);

    List<Post> posts = postRepository.findAllByAuthorOrderByCreatedAtDesc(user);

    return posts.stream().map(PostResponseDto::new).toList();
  }
}
