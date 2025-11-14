package com.likelion.nextworld.domain.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.entity.WorkStatistics;

public interface WorkStatisticsRepository extends JpaRepository<WorkStatistics, Long> {

  Optional<WorkStatistics> findByWork(Work work);
}
