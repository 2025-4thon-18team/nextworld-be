package com.likelion.nextworld.domain.post.repository;

import com.likelion.nextworld.domain.post.entity.Comment;
import com.likelion.nextworld.domain.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findAllByPostOrderByIdAsc(Post post);
}
