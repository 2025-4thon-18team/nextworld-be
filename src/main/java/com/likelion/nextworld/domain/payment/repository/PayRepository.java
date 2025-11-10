package com.likelion.nextworld.domain.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.nextworld.domain.payment.entity.Pay;
import com.likelion.nextworld.domain.payment.entity.PayStatus;
import com.likelion.nextworld.domain.user.entity.User;

public interface PayRepository extends JpaRepository<Pay, Long> {
  List<Pay> findByPayerOrderByCreatedAtDesc(User payer);

  Optional<Pay> findByImpUid(String impUid);

  List<Pay> findByPayerAndPayStatusOrderByCreatedAtDesc(User payer, PayStatus status);

  List<Pay> findByPayStatus(PayStatus status);
}
