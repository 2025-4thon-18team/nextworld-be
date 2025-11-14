package com.likelion.nextworld.domain.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.PostTag;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
  List<PostTag> findByPost(Post post);

  void deleteByPost(Post post);
}
