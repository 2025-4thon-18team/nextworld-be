package com.likelion.nextworld.global.ai;

import org.springframework.stereotype.Service;

import com.likelion.nextworld.domain.post.entity.WorkGuideline;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiCheckService {

  private final OpenAiClient openAiClient;

  public String validateContent(String content, WorkGuideline guideline) {

    // 1) 금지어 (로컬 필터)
    if (guideline.getWord() != null) {
      String[] banned = guideline.getWord().split(",");
      for (String w : banned) {
        String clean = w.trim();
        if (!clean.isEmpty() && content.contains(clean)) {
          return "VIOLATION: 금지어 '" + clean + "'가 포함되어 있습니다.";
        }
      }
    }

    // 2) 프롬프트 생성
    String prompt =
        """
                너는 웹소설 규칙 검수 AI다.

                <가이드라인>
                세계관 관계 규칙: %s
                내용 규칙: %s
                배경 설정: %s
                금지어: %s

                <검수할 글>
                %s

                규칙/금지어 위반이면 반드시:
                VIOLATION: {이유}

                문제가 없으면:
                OK
                """
            .formatted(
                guideline.getGuidelineRelation(),
                guideline.getGuidelineContent(),
                guideline.getGuidelineBackground(),
                guideline.getWord(),
                content);

    // 3) OpenAI API 호출
    String result = openAiClient.analyze(prompt);

    return result.trim();
  }
}
