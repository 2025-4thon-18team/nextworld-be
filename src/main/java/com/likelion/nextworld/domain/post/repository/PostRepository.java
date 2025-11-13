package com.likelion.nextworld.domain.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.WorkStatus;
import com.likelion.nextworld.domain.user.entity.User;

public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findByAuthorAndStatus(User author, WorkStatus status);

  Optional<Post> findByIdAndAuthorAndStatus(Long id, User author, WorkStatus status);

  List<Post> findAllByAuthorOrderByCreatedAtDesc(User author);

  List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
      String titleKeyword, String contentKeyword);

  List<Post> findAllByOrderByCreatedAtDesc();
}
