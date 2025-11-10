package com.likelion.nextworld.domain.post.entity;

public enum WorkGenre {
  ROMANCE("로맨스"),
  FANTASY("판타지"),
  THRILLER("스릴러"),
  MARTIAL_ARTS("무협"),
  DRAMA("드라마"),
  SF("SF"),
  COMEDY("코미디");

  private final String label;

  WorkGenre(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
