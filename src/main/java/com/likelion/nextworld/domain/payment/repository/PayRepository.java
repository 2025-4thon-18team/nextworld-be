package com.likelion.nextworld.domain.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.payment.entity.Pay;
import com.likelion.nextworld.domain.payment.entity.PayStatus;
import com.likelion.nextworld.domain.payment.entity.TransactionType;
import com.likelion.nextworld.domain.user.entity.User;

public interface PayRepository extends JpaRepository<Pay, Long> {
  List<Pay> findByPayerOrderByCreatedAtDesc(User payer);

  Optional<Pay> findByImpUid(String impUid);

  List<Pay> findByPayerAndTransactionTypeOrderByCreatedAtDesc(User payer, TransactionType type);

  List<Pay> findByPayStatus(PayStatus status);

  // 결제한 Post 조회 (USE 타입이고 post가 NULL이 아닌 경우)
  List<Pay> findByPayerAndTransactionTypeAndPostIsNotNullAndPayStatusOrderByCreatedAtDesc(
      User payer, TransactionType type, PayStatus status);
}
