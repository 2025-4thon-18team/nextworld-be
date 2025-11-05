package com.likelion.nextworld.domain.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.DerivativeWork;
import com.likelion.nextworld.domain.post.entity.WorkStatus;
import com.likelion.nextworld.domain.user.entity.User;

public interface DerivativeWorkRepository extends JpaRepository<DerivativeWork, Long> {
  List<DerivativeWork> findByAuthorAndStatus(User author, WorkStatus status);

  Optional<DerivativeWork> findByIdAndAuthorAndStatus(Long id, User author, WorkStatus status);
}
