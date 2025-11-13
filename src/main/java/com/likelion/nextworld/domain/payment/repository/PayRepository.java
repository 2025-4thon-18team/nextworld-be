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

  List<Pay> findByPayerAndTypeOrderByCreatedAtDesc(User payer, TransactionType type);

  List<Pay> findByStatus(PayStatus status);

  List<Pay> findByPayerAndTypeAndPostIdIsNotNullOrderByCreatedAtDesc(
      User payer, TransactionType type);

  List<Pay> findByPayerAndTypeAndWorkIdIsNotNullOrderByCreatedAtDesc(
      User payer, TransactionType type);
}
