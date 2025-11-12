package com.likelion.nextworld.domain.scrap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.scrap.entity.Scrap;
import com.likelion.nextworld.domain.user.entity.User;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

  boolean existsByUserAndWork(User user, Work work);

  boolean existsByUserAndPost(User user, Post post);

  Optional<Scrap> findByUserAndWork(User user, Work work);

  Optional<Scrap> findByUserAndPost(User user, Post post);

  List<Scrap> findAllByUser(User user);
}
