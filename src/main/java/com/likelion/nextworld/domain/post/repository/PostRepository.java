package com.likelion.nextworld.domain.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.PostType;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.entity.WorkStatus;
import com.likelion.nextworld.domain.user.entity.User;

public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findByAuthorAndStatus(User author, WorkStatus status);

  Optional<Post> findByIdAndAuthorAndStatus(Long id, User author, WorkStatus status);

  // 작품의 회차 목록
  List<Post> findByWorkOrderByEpisodeNumberAsc(Work work);

  // 독립 포스트 목록 (work가 NULL)
  List<Post> findByWorkIsNull();

  // 원작 참조 포스트 목록
  List<Post> findByParentWork(Work parentWork);

  // 포스트 타입으로 필터링
  List<Post> findByPostType(PostType postType);

  @Query("SELECT MAX(p.episodeNumber) FROM Post p WHERE p.work.id = :workId")
  Integer findMaxEpisodeNumberByWorkId(@Param("workId") Long workId);
}
