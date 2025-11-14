package com.likelion.nextworld.domain.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.entity.WorkTag;

public interface WorkTagRepository extends JpaRepository<WorkTag, Long> {
  List<WorkTag> findByWork(Work work);

  void deleteByWork(Work work);
}
