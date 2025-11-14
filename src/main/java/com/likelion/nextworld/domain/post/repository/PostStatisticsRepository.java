package com.likelion.nextworld.domain.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.PostStatistics;

public interface PostStatisticsRepository extends JpaRepository<PostStatistics, Long> {
  Optional<PostStatistics> findByPost(Post post);
}
