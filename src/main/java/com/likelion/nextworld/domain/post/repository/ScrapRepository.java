package com.likelion.nextworld.domain.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.Scrap;
import com.likelion.nextworld.domain.user.entity.User;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

  Optional<Scrap> findByUserAndPostId(User user, Long postId);

  boolean existsByUserAndPostId(User user, Long postId);

  void deleteByUserAndPostId(User user, Long postId);
}
