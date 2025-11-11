package com.likelion.nextworld.global.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DelimiterUtil {
  private static final String DELIMITER = "|";

  /**
   * 배열 → 구분자 문자열
   *
   * @param list 문자열 리스트
   * @return 구분자로 연결된 문자열 (null 또는 빈 리스트인 경우 null 반환)
   */
  public static String arrayToDelimited(List<String> list) {
    if (list == null || list.isEmpty()) {
      return null;
    }
    return list.stream()
        .filter(Objects::nonNull)
        .filter(s -> !s.trim().isEmpty())
        .collect(Collectors.joining(DELIMITER));
  }

  /**
   * 구분자 문자열 → 배열
   *
   * @param str 구분자로 연결된 문자열
   * @return 문자열 리스트 (null 또는 빈 문자열인 경우 빈 리스트 반환)
   */
  public static List<String> delimitedToArray(String str) {
    if (str == null || str.trim().isEmpty()) {
      return new ArrayList<>();
    }
    return Arrays.stream(str.split("\\" + DELIMITER))
        .filter(s -> !s.trim().isEmpty())
        .collect(Collectors.toList());
  }
}
