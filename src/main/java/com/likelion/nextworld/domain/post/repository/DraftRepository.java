package com.likelion.nextworld.domain.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.Draft;
import com.likelion.nextworld.domain.post.entity.DraftType;
import com.likelion.nextworld.domain.user.entity.User;

public interface DraftRepository extends JpaRepository<Draft, Long> {

  List<Draft> findByAuthor(User author);

  List<Draft> findByAuthorAndDraftType(User author, DraftType draftType);

  Optional<Draft> findByIdAndAuthor(Long id, User author);

  List<Draft> findByWorkId(Long workId);

  List<Draft> findByPostId(Long postId);
}
