package com.likelion.nextworld.domain.scrap.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Check;

import com.likelion.nextworld.domain.post.entity.Post;
import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.user.entity.User;
import com.likelion.nextworld.global.common.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
    name = "scraps",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_scrap_user_work",
          columnNames = {"user_id", "work_id"}),
      @UniqueConstraint(
          name = "uq_scrap_user_post",
          columnNames = {"user_id", "post_id"})
    })
@Check(
    constraints =
        "((work_id is not null and post_id is null) or (work_id is null and post_id is not null))")
public class Scrap extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "work_id")
  private Work work;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id")
  private Post post;

  public boolean isWorkTarget() {
    return work != null;
  }

  public boolean isPostTarget() {
    return post != null;
  }
}
