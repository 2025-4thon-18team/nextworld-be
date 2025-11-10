package com.likelion.nextworld.domain.post.dto;

import com.likelion.nextworld.domain.post.entity.WorkGenre;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkRequestDto {
    private String title;
    private String description;
    private String coverImageUrl;
    private List<String> tags;
    private WorkGenre genre;

    private String universeDescription;

    private Boolean allowDerivative;
    private String guidelineRelation;
    private String guidelineContent;
    private String guidelineBackground;
    private List<String> bannedWords;

    private Boolean isPaid;
    private Boolean allowDerivativeProfit;
}
