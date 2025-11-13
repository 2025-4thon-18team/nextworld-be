package com.likelion.nextworld.domain.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.user.entity.User;

public interface WorkRepository extends JpaRepository<Work, Long> {

  // 제목으로 검색
  List<Work> findByTitleContaining(String keyword);

  // 작가(author_id)로 검색
  List<Work> findByAuthorUserId(Long userId);

  // 2차 창작 허용된 작품만
  List<Work> findByAllowDerivativeTrue();

  List<Work> findAllByAuthor(User author);

  List<Work> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
      String titleKeyword, String descriptionKeyword);

  List<Work> findAllByOrderByIdDesc();
}
