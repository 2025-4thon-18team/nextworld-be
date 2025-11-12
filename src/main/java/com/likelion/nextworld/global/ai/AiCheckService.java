package com.likelion.nextworld.global.ai;

import org.springframework.stereotype.Service;

import com.likelion.nextworld.domain.post.entity.Work;
import com.likelion.nextworld.domain.post.repository.WorkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiCheckService {

  private final WorkRepository workRepository;
  private final OpenAiClient openAiClient;

  /** workId를 기반으로 1차 작품의 가이드라인을 불러와 post content를 검수하는 메서드 */
  public boolean validatePostById(Long workId, String content) {
    Work work =
        workRepository
            .findById(workId)
            .orElseThrow(() -> new IllegalArgumentException("해당 작품을 찾을 수 없습니다."));

    return validatePost(work, content);
  }

  /** 가이드라인과 금지어를 기반으로 실제 AI 검수 수행 */
  public boolean validatePost(Work work, String content) {

    // 1️⃣ 금지어 직접 필터링
    if (work.getBannedWords() != null) {
      for (String word : work.getBannedWords().split("\\|")) { // '|' 구분자로 분리
        if (content.contains(word)) {
          return false;
        }
      }
    }

    // 2️⃣ AI 검수 프롬프트
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
                work.getGuidelineBackground(),
                work.getGuidelineRelation(),
                work.getGuidelineContent(),
                work.getBannedWords(),
                content);

    // 3️⃣ OpenAI API 요청 (결과값: "OK" 또는 "VIOLATION")
    String result = openAiClient.analyze(prompt);

    return result.trim().equalsIgnoreCase("OK");
  }
}
