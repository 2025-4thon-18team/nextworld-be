package com.likelion.nextworld.domain.revenue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.revenue.entity.RevenueSettlementHistory;
import com.likelion.nextworld.domain.user.entity.User;

public interface RevenueSettlementHistoryRepository
    extends JpaRepository<RevenueSettlementHistory, Long> {

  List<RevenueSettlementHistory> findByAuthorOrderBySettledAtDesc(User author);
}
