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
      "SELECT COALESCE(SUM(CASE WHEN r.derivativeAuthor = :user THEN r.derivativeAuthorAmount ELSE 0 END) + "
          + "SUM(CASE WHEN r.originalAuthor = :user THEN r.originalAuthorAmount ELSE 0 END), 0) "
          + "FROM RevenueShare r WHERE r.derivativeAuthor = :user OR r.originalAuthor = :user")
  long sumAmountByDerivativeAuthorOrOriginalAuthor(@Param("user") User user);

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

  // 사용자가 구매한 Post 조회 (Pay의 payer가 해당 사용자인 RevenueShare 조회)
  @Query(
      """
                    SELECT DISTINCT r.post
                    FROM RevenueShare r
                    JOIN r.pay p
                    WHERE p.payer = :user AND p.type = :type AND p.status = :status
                    ORDER BY p.createdAt DESC
                    """)
  List<com.likelion.nextworld.domain.post.entity.Post> findPurchasedPostsByUser(
      @Param("user") User user,
      @Param("type") com.likelion.nextworld.domain.payment.entity.TransactionType type,
      @Param("status") com.likelion.nextworld.domain.payment.entity.PayStatus status);
}
