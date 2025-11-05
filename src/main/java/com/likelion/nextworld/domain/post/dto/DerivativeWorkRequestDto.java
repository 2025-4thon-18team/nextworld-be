package com.likelion.nextworld.domain.post.dto;

import com.likelion.nextworld.domain.post.entity.CreationType;
import com.likelion.nextworld.domain.post.entity.WorkStatus;
import com.likelion.nextworld.domain.post.entity.WorkType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DerivativeWorkRequestDto {
    private String title;
    private String content;
    private WorkType workType; // 단편 / 장편
    private CreationType creationType; // 오리지널 / 2차창작
    private Long parentId;
    private WorkStatus status;
}
