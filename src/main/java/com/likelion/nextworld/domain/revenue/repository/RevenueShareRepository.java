package com.likelion.nextworld.domain.revenue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion.nextworld.domain.revenue.entity.RevenueShare;
import com.likelion.nextworld.domain.user.entity.User;

public interface RevenueShareRepository extends JpaRepository<RevenueShare, Long> {

  long countByDerivativeAuthorOrOriginalAuthor(User derivativeAuthor, User originalAuthor);

  default long countByDerivativeAuthorOrOriginalAuthor(User user) {
    return countByDerivativeAuthorOrOriginalAuthor(user, user);
  }

  @Query(
      "SELECT COALESCE(SUM(r.shareEach), 0) FROM RevenueShare r WHERE r.derivativeAuthor = :user OR r.originalAuthor = :user")
  long sumShareEachByDerivativeAuthorOrOriginalAuthor(@Param("user") User user);

  @Query(
      """
                    SELECT r
                    FROM RevenueShare r
                    JOIN FETCH r.pay p
                    JOIN FETCH r.post post
                    JOIN FETCH p.payer buyer
                    WHERE r.derivativeAuthor = :user OR r.originalAuthor = :user
                    ORDER BY p.createdAt DESC
                    """)
  List<RevenueShare> findSalesByDerivativeAuthorOrOriginalAuthor(@Param("user") User user);
}
