package com.likelion.nextworld.domain.revenue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion.nextworld.domain.revenue.entity.RevenueShare;
import com.likelion.nextworld.domain.user.entity.User;

public interface RevenueShareRepository extends JpaRepository<RevenueShare, Long> {
  long countByAuthor(User author);

  @Query("SELECT COALESCE(SUM(r.shareAmount), 0) FROM RevenueShare r WHERE r.author = :author")
  long sumShareAmountByAuthor(User author);

  @Query(
      """
                    SELECT r
                    FROM RevenueShare r
                    JOIN FETCH r.pay p
                    JOIN FETCH p.post post
                    JOIN FETCH p.payer buyer
                    WHERE r.author = :author
                    ORDER BY p.createdAt DESC
                    """)
  List<RevenueShare> findSalesByAuthor(@Param("author") User author);

  @Query(
      "SELECT COALESCE(SUM(r.shareAmount), 0) FROM RevenueShare r WHERE r.author = :author AND r.settled = false")
  long findUnsettledAmountByAuthor(User author);

  List<RevenueShare> findByAuthorAndSettledFalse(User author);
}
