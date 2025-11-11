package com.likelion.nextworld.domain.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.post.entity.Report;
import com.likelion.nextworld.domain.post.entity.ReportStatus;

public interface ReportRepository extends JpaRepository<Report, Long> {

  List<Report> findByReportStatus(ReportStatus reportStatus);

  List<Report> findByWorkId(Long workId);

  List<Report> findByPostId(Long postId);
}
