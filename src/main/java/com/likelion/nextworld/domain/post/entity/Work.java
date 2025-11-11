package com.likelion.nextworld.domain.post.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.likelion.nextworld.domain.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "works")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "derivativePosts") // ✅ 무한루프 방지
public class Work {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 1. 기본 정보
  private String title;
  private String description;
  private String coverImageUrl;

  @ElementCollection private List<String> tags = new ArrayList<>();

  // 2. 세계관 설정
  @Column(columnDefinition = "TEXT")
  private String universeDescription;

  // 3. 2차 창작 관련
  private Boolean allowDerivative;

  @Column(columnDefinition = "TEXT")
  private String guidelineRelation;

  @Column(columnDefinition = "TEXT")
  private String guidelineContent;

  @Column(columnDefinition = "TEXT")
  private String guidelineBackground;

  @ElementCollection private List<String> bannedWords = new ArrayList<>();

  // 4. 수익 관련
  private Boolean isPaid;
  private Long price; // 금액
  private Boolean allowDerivativeProfit;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  private User author;

  //  2차 창작물(Post) 연결
  @OneToMany(mappedBy = "parentWork", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> derivativePosts = new ArrayList<>();

  // 양방향 편의 메서드
  public void addDerivativePost(Post post) {
    derivativePosts.add(post);
    post.setParentWork(this);
  }
}
