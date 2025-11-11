package com.likelion.nextworld.domain.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.Scrap;
import com.likelion.nextworld.domain.user.entity.User;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

  Optional<Scrap> findByUserAndWorkId(User user, Long workId);

  Optional<Scrap> findByUserAndPostId(User user, Long postId);

  boolean existsByUserAndWorkId(User user, Long workId);

  boolean existsByUserAndPostId(User user, Long postId);

  void deleteByUserAndWorkId(User user, Long workId);

  void deleteByUserAndPostId(User user, Long postId);
}
