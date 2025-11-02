package com.likelion.nextworld.domain.user.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class TokenBlacklist {

  private final Set<String> blacklistedTokens = new HashSet<>();

  public void add(String token) {
    blacklistedTokens.add(token);
  }

  public boolean contains(String token) {
    return blacklistedTokens.contains(token);
  }
}
