package com.gcp.assignment.service;

import com.gcp.assignment.configuer.GeminiProperties;
import com.gcp.assignment.dto.MusicRequest;
import com.gcp.assignment.dto.MusicResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MusicRecommendationService {

    private final GeminiProperties geminiProperties;
    private final RestClient geminiClient;
    private final ObjectMapper objectMapper;

    public MusicResponse getMusicRecommendation(MusicRequest request) {
        try {
            String prompt = buildPrompt(request);

            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            Map<String, Object> parts = new HashMap<>();

            parts.put("text", prompt);
            contents.put("parts", new Object[]{parts});
            requestBody.put("contents", new Object[]{contents});

            String jsonResponse = geminiClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            // JSON 응답에서 필요한 부분 추출
            JsonNode responseNode = objectMapper.readTree(jsonResponse);
            String textResponse = responseNode.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            // JSON 형식의 응답에서 중괄호 부분만 추출
            String jsonPart = extractJsonFromText(textResponse);

            // JSON을 MusicResponse 객체로 변환
            return objectMapper.readValue(jsonPart, MusicResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return new MusicResponse(); // 오류 발생 시 빈 응답 반환
        }
    }

    private String buildPrompt(MusicRequest request) {
        return String.format("""
                오늘 날씨는 맑음, 계절은 %s이야.
                내 기분은 %s이고, 오늘은 %s와 같은 일이 있었어.
                내가 좋아하는 아티스트는 %s, 장르는 %s야.
                이 모든 걸 고려해서 노래를 %d곡 추천해줘.
                추천된 노래는 JSON 형식으로만 반환해줘.
                JSON 형식은 다음과 같아.
                {
                  "playlist": [
                    {
                      "title": "노래 제목",
                      "artist": "아티스트"
                    }
                  ],
                  "comment": "이렇게 추천한 이유에 대한 코멘트"
                }
                """,
                getSeason(),
                request.getMood(),
                request.getStory(),
                String.join(", ", request.getFavoriteArtists()),
                String.join(", ", request.getFavoriteGenres()),
                request.getNumberOfSongs()
        );
    }

    private String extractJsonFromText(String text) {
        // JSON 형식의 데이터 찾기 (중괄호로 둘러싸인 부분)
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}") + 1;

        if (start >= 0 && end > start) {
            return text.substring(start, end);
        }

        // JSON을 찾을 수 없는 경우 빈 객체 반환
        return "{}";
    }

    private String getSeason() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 3 && month <= 5) {
            return "봄";
        } else if (month >= 6 && month <= 8) {
            return "여름";
        } else if (month >= 9 && month <= 11) {
            return "가을";
        } else {
            return "겨울";
        }
    }
}
