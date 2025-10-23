package com.gcp.assignment.service;

import com.gcp.assignment.dto.MusicRequest;
import com.gcp.assignment.dto.MusicResponse;
import com.gcp.assignment.exception.MusicRecommendationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicRecommendationService {

    private final Client geminiClient;
    private final ObjectMapper objectMapper;

    public MusicResponse getMusicRecommendation(MusicRequest request) {
        try {
            validateRequest(request);
            String prompt = buildPrompt(request);
            log.info("Gemini API 호출 시작");

            // Gemini API 호출
            GenerateContentResponse response = geminiClient.models.generateContent(
                    "gemini-2.0-flash-exp",
                    prompt,
                    null
            );

            String textResponse = response.text();

            if (textResponse == null || textResponse.trim().isEmpty()) {
                throw new MusicRecommendationException("AI로부터 응답을 받지 못했습니다.");
            }

            log.debug("Gemini API 응답 수신: {} characters", textResponse.length());

            // JSON 형식의 응답에서 중괄호 부분만 추출
            String jsonPart = extractJsonFromText(textResponse);

            // JSON을 MusicResponse 객체로 변환
            MusicResponse musicResponse = objectMapper.readValue(jsonPart, MusicResponse.class);

            if (musicResponse.getPlaylist() == null || musicResponse.getPlaylist().isEmpty()) {
                throw new MusicRecommendationException("추천된 노래가 없습니다. 다시 시도해주세요.");
            }

            log.info("음악 추천 완료: {} 곡", musicResponse.getPlaylist().size());
            return musicResponse;

        } catch (MusicRecommendationException e) {
            throw e;
        } catch (Exception e) {
            log.error("음악 추천 중 오류 발생", e);
            throw new MusicRecommendationException("음악 추천 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        }
    }

    private void validateRequest(MusicRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청 정보가 없습니다.");
        }
        if (request.getNumberOfSongs() != null && (request.getNumberOfSongs() < 1 || request.getNumberOfSongs() > 10)) {
            throw new IllegalArgumentException("추천 곡 수는 1~10 사이여야 합니다.");
        }
    }

    private String buildPrompt(MusicRequest request) {
        String artists = (request.getFavoriteArtists() != null && !request.getFavoriteArtists().isEmpty())
            ? String.join(", ", request.getFavoriteArtists())
            : "없음";
        String genres = (request.getFavoriteGenres() != null && !request.getFavoriteGenres().isEmpty())
            ? String.join(", ", request.getFavoriteGenres())
            : "없음";

        return String.format("""
                오늘 날씨는 맑음, 계절은 %s이야.
                내 기분은 %s이고, 오늘은 %s와 같은 일이 있었어.
                내가 좋아하는 아티스트는 %s, 장르는 %s야.
                꼭 좋아하는 아티스트의 노래만 추천할 필요는 없어.
                이 모든 걸 고려해서 노래를 %d곡 추천해줘.
                추천된 노래는 반드시 JSON 형식으로만 반환해줘. 다른 텍스트는 포함하지 마.
                JSON 형식은 다음과 같아.
                {
                  "playlist": [
                    {
                      "title": "노래 제목",
                      "artist": "아티스트"
                    }
                  ],
                  "comment": "이렇게 추천한 이유에 대한 자세한 코멘트"
                }
                """,
                getSeason(),
                request.getMood(),
                request.getStory(),
                artists,
                genres,
                request.getNumberOfSongs()
        );
    }

    private String extractJsonFromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new MusicRecommendationException("AI 응답이 비어있습니다.");
        }

        // JSON 형식의 데이터 찾기 (중괄호로 둘러싸인 부분)
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}") + 1;

        if (start >= 0 && end > start) {
            return text.substring(start, end);
        }

        throw new MusicRecommendationException("AI 응답에서 JSON 데이터를 찾을 수 없습니다. 다시 시도해주세요.");
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
