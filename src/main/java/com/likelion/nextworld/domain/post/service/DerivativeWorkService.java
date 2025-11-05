package com.likelion.nextworld.domain.post.service;

import com.likelion.nextworld.domain.post.dto.DerivativeWorkRequestDto;
import com.likelion.nextworld.domain.post.dto.DerivativeWorkResponseDto;
import com.likelion.nextworld.domain.post.entity.DerivativeWork;
import com.likelion.nextworld.domain.post.entity.WorkStatus;
import com.likelion.nextworld.domain.post.repository.DerivativeWorkRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DerivativeWorkService {

    private final DerivativeWorkRepository derivativeWorkRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    //  토큰에서 유저 추출
    private User getUserFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("유효하지 않은 토큰 형식입니다.");
        }

        String actualToken = token.substring(7); // 'Bearer ' 제거
        String email = jwtTokenProvider.getEmailFromToken(actualToken);

        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    //  2차 작품 등록 (최종 등록)
    @Transactional
    public DerivativeWorkResponseDto createWork(DerivativeWorkRequestDto request, String token) {
        User currentUser = getUserFromToken(token);

        DerivativeWork parentWork = null;
        if (request.getParentId() != null) {
            parentWork = derivativeWorkRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("원작을 찾을 수 없습니다."));
        }

        DerivativeWork work = DerivativeWork.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(currentUser)
                .status(request.getStatus())
                .creationType(request.getCreationType())
                .workType(request.getWorkType())
                .parentWork(parentWork) // ✅ 부모 연결
                .build();

        DerivativeWork saved = derivativeWorkRepository.save(work);
        return toDto(saved);
    }

    //  임시저장
    @Transactional
    public DerivativeWorkResponseDto saveDraft(DerivativeWorkRequestDto request, String token) {
        User currentUser = getUserFromToken(token);

        DerivativeWork draft =
                DerivativeWork.builder()
                        .title(request.getTitle())
                        .content(request.getContent())
                        .author(currentUser)
                        .status(WorkStatus.DRAFT)
                        .workType(request.getWorkType())
                        .creationType(request.getCreationType())
                        .build();

        DerivativeWork saved = derivativeWorkRepository.save(draft);
        return toDto(saved);
    }

    // DTO 변환
    private DerivativeWorkResponseDto toDto(DerivativeWork work) {
        return DerivativeWorkResponseDto.builder()
                .id(work.getId())
                .title(work.getTitle())
                .content(work.getContent())
                .authorName(work.getAuthor() != null ? work.getAuthor().getNickname() : null)
                .status(work.getStatus())
                .workType(work.getWorkType())
                .creationType(work.getCreationType())
                .createdAt(work.getCreatedAt())
                .updatedAt(work.getUpdatedAt())
                .build();
    }

    // 임시저장 전체 조회 (본인 것만)
    @Transactional(readOnly = true)
    public List<DerivativeWorkResponseDto> getAllDrafts(String token) {
        User currentUser = getUserFromToken(token); // JWT 토큰에서 유저 찾기
        List<DerivativeWork> drafts =
                derivativeWorkRepository.findByAuthorAndStatus(currentUser, WorkStatus.DRAFT);
        return drafts.stream().map(this::toDto).collect(Collectors.toList());
    }

    // 단일 임시저장 조회 (본인 것만)
    @Transactional(readOnly = true)
    public DerivativeWorkResponseDto getDraftById(Long id, String token) {
        User currentUser = getUserFromToken(token);
        DerivativeWork draft =
                derivativeWorkRepository
                        .findByIdAndAuthorAndStatus(id, currentUser, WorkStatus.DRAFT)
                        .orElseThrow(() -> new RuntimeException("본인의 임시저장 글이 아니거나 존재하지 않습니다."));
        return toDto(draft);
    }

    // 작품 수정
    @Transactional
    public DerivativeWorkResponseDto updateWork(
            Long id, DerivativeWorkRequestDto request, String token) {
        User currentUser = getUserFromToken(token); // 로그인한 사용자
        DerivativeWork work =
                derivativeWorkRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("해당 작품을 찾을 수 없습니다. ID: " + id));

        // 작성자 검증
        if (!work.getAuthor().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        // 수정 가능한 필드만 업데이트
        work.setTitle(request.getTitle());
        work.setContent(request.getContent());

        DerivativeWork updated = derivativeWorkRepository.save(work);
        return toDto(updated);
    }

    // 작품 삭제
    @Transactional
    public void deleteWork(Long id, String token) {
        User currentUser = getUserFromToken(token); //  로그인한 사용자
        DerivativeWork work =
                derivativeWorkRepository
                        .findById(id)
                        .orElseThrow(() -> new RuntimeException("해당 작품을 찾을 수 없습니다. ID: " + id));

        // 작성자 검증
        if (!work.getAuthor().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }

        derivativeWorkRepository.delete(work);
    }
}
