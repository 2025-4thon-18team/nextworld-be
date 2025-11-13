package com.likelion.nextworld.global.ai;

import org.springframework.stereotype.Service;

import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.entity.WorkGuideline;
import com.likelion.nextworld.domain.post.repository.WorkGuidelineRepository;
import com.likelion.nextworld.domain.post.repository.WorkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiCheckService {

  private final WorkRepository workRepository;
  private final WorkGuidelineRepository workGuidelineRepository;
  private final OpenAiClient openAiClient;

  /** workId를 기반으로 작품과 해당 작품의 가이드라인을 불러와 post content를 검수하는 메서드 */
  public boolean validatePostById(Long workId, String content) {
    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new IllegalArgumentException("해당 작품을 찾을 수 없습니다."));

    WorkGuideline guideline =
        workGuidelineRepository
            .findById(workId) // WorkGuideline 의 PK = work_id (@MapsId)
            .orElseThrow(() -> new IllegalArgumentException("해당 작품의 가이드라인을 찾을 수 없습니다."));

    return validatePost(work, guideline, content);
  }

  /** 가이드라인과 금지어를 기반으로 실제 AI 검수 수행 */
  public boolean validatePost(Work work, WorkGuideline guideline, String content) {

    // 1️⃣ 금지어 직접 필터링
    String bannedWords = guideline.getWord(); // 금지어 필드
    if (bannedWords != null && !bannedWords.isBlank()) {
      for (String word : bannedWords.split("\\|")) { // '|' 구분자로 분리
        if (!word.isBlank() && content.contains(word)) {
          return false;
        }
      }
    }

    // 2️⃣ AI 검수 프롬프트 구성
    String prompt =
        """
                        다음은 한 작품의 가이드라인과 금지어 목록입니다.
                        ---
                        [배경] %s
                        [관계] %s
                        [내용] %s
                        [금지어] %s
                        ---
                        아래 글이 위 가이드라인이나 금지어를 위반했는지 판단해줘.
                        위반이면 "VIOLATION", 안전하면 "OK"만 답해.
                        글 내용:
                        %s
                        """
            .formatted(
                guideline.getGuidelineBackground(),
                guideline.getGuidelineRelation(),
                guideline.getGuidelineContent(),
                bannedWords != null ? bannedWords : "",
                content);

    // 3️⃣ OpenAI API 요청 (결과값: "OK" 또는 "VIOLATION")
    String result = openAiClient.analyze(prompt);

    return result != null && result.trim().equalsIgnoreCase("OK");
  }
}
