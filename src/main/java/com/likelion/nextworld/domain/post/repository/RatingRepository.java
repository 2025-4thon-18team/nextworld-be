package com.likelion.nextworld.domain.post.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.Rating;
import com.likelion.nextworld.domain.user.entity.User;

public interface RatingRepository extends JpaRepository<Rating, Long> {

  Optional<Rating> findByUserAndPost(User user, Post post);

  Long countByPost(Post post);

  @Query("SELECT COALESCE(AVG(r.score), 0) FROM Rating r WHERE r.post = :post")
  BigDecimal findAverageScoreByPost(Post post);
}
