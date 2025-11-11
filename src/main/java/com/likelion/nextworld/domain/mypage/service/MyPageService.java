package com.likelion.nextworld.domain.mypage.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.likelion.nextworld.domain.mypage.dto.ProfileUpdateRequest;
import com.likelion.nextworld.domain.payment.dto.PayItemResponse;
import com.likelion.nextworld.domain.payment.dto.PointsResponse;
import com.likelion.nextworld.domain.payment.service.PaymentService;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.service.S3Uploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {
  private final UserRepository userRepository;
  private final PaymentService paymentService;
  private final S3Uploader s3Uploader;

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
}
