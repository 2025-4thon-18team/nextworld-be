package com.likelion.nextworld.domain.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.like.entity.Like;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.user.entity.User;

public interface LikeRepository extends JpaRepository<Like, Long> {

  boolean existsByUserAndWork(User user, Work work);

  Optional<Like> findByUserAndWork(User user, Work work);

  List<Like> findAllByUser(User user);
}
