package com.likelion.nextworld.domain.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.Like;
import com.likelion.nextworld.domain.user.entity.User;

public interface LikeRepository extends JpaRepository<Like, Long> {

  Optional<Like> findByUserAndWorkId(User user, Long workId);

  boolean existsByUserAndWorkId(User user, Long workId);

  void deleteByUserAndWorkId(User user, Long workId);
}
