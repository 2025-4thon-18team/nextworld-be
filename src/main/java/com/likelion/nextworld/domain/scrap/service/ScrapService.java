package com.likelion.nextworld.domain.scrap.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.exception.WorkErrorCode;
import com.likelion.nextworld.domain.post.repository.PostRepository;
import com.likelion.nextworld.domain.post.repository.WorkRepository;
import com.likelion.nextworld.domain.scrap.dto.ScrapResponse;
import com.likelion.nextworld.domain.scrap.entity.Scrap;
import com.likelion.nextworld.domain.scrap.exception.ScrapErrorCode;
import com.likelion.nextworld.domain.scrap.mapper.ScrapMapper;
import com.likelion.nextworld.domain.scrap.repository.ScrapRepository;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.domain.user.exception.UserErrorCode;
import com.likelion.nextworld.domain.user.repository.UserRepository;
import com.likelion.nextworld.domain.user.security.UserPrincipal;
import com.likelion.nextworld.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ScrapService {

  private final ScrapRepository scrapRepository;
  private final ScrapMapper scrapMapper;
  private final UserRepository userRepository;
  private final WorkRepository workRepository;
  private final PostRepository postRepository;

  private User getCurrentUser(UserPrincipal principal) {
    if (principal == null || principal.getId() == null) {
      throw new CustomException(UserErrorCode.USER_NOT_FOUND);
    }
    return userRepository
        .findById(principal.getId())
        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
  }

  /** WORK 스크랩 생성 */
  public ScrapResponse createWorkScrap(Long workId, UserPrincipal principal) {
    User user = getCurrentUser(principal);
    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new CustomException(WorkErrorCode.WORK_NOT_FOUND));

    if (scrapRepository.existsByUserAndWork(user, work)) {
      throw new CustomException(ScrapErrorCode.ALREADY_SCRAPPED);
    }

    Scrap scrap = Scrap.builder().user(user).work(work).build();
    return scrapMapper.toResponse(scrapRepository.save(scrap));
  }

  /** POST 스크랩 생성 */
  public ScrapResponse createPostScrap(Long postId, UserPrincipal principal) {
    User user = getCurrentUser(principal);
    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(
                () -> new CustomException(WorkErrorCode.WORK_NOT_FOUND)); // 필요시 PostErrorCode로 분리

    if (scrapRepository.existsByUserAndPost(user, post)) {
      throw new CustomException(ScrapErrorCode.ALREADY_SCRAPPED);
    }

    Scrap scrap = Scrap.builder().user(user).post(post).build();
    return scrapMapper.toResponse(scrapRepository.save(scrap));
  }

  /** WORK 스크랩 삭제 */
  public void deleteWorkScrap(Long workId, UserPrincipal principal) {
    User user = getCurrentUser(principal);
    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new CustomException(WorkErrorCode.WORK_NOT_FOUND));

    Scrap scrap =
        scrapRepository
            .findByUserAndWork(user, work)
            .orElseThrow(() -> new CustomException(ScrapErrorCode.SCRAP_NOT_FOUND));

    scrapRepository.delete(scrap);
  }

  /** POST 스크랩 삭제 */
  public void deletePostScrap(Long postId, UserPrincipal principal) {
    User user = getCurrentUser(principal);
    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(
                () -> new CustomException(WorkErrorCode.WORK_NOT_FOUND)); // 필요시 PostErrorCode

    Scrap scrap =
        scrapRepository
            .findByUserAndPost(user, post)
            .orElseThrow(() -> new CustomException(ScrapErrorCode.SCRAP_NOT_FOUND));

    scrapRepository.delete(scrap);
  }

  /** 엔티티 리스트만 필요할 때 */
  public List<Scrap> getMyScrapEntities(UserPrincipal principal) {
    User user = getCurrentUser(principal);
    return scrapRepository.findAllByUser(user);
  }
}
