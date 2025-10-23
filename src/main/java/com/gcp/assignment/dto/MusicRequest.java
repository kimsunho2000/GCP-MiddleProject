package com.gcp.assignment.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MusicRequest {

    @NotBlank(message = "기분을 입력해주세요.")
    @Size(max = 100, message = "기분은 100자 이내로 입력해주세요.")
    private String mood;

    @NotNull(message = "추천받을 노래 수를 입력해주세요.")
    @Min(value = 1, message = "최소 1곡 이상 선택해주세요.")
    @Max(value = 10, message = "최대 10곡까지 추천받을 수 있습니다.")
    private Integer numberOfSongs;

    @Size(max = 5, message = "좋아하는 아티스트는 최대 5명까지 입력 가능합니다.")
    private List<String> favoriteArtists;

    @Size(max = 5, message = "좋아하는 장르는 최대 5개까지 입력 가능합니다.")
    private List<String> favoriteGenres;

    @NotBlank(message = "오늘 있었던 일을 입력해주세요.")
    @Size(max = 500, message = "이야기는 500자 이내로 입력해주세요.")
    private String story;
}
