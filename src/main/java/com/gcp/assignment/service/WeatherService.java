package com.gcp.assignment.service;

import com.gcp.assignment.dto.KmaWeatherApiResponse;
import com.gcp.assignment.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${KMA-API-KEY}")
    private String apiKey;

    @Value("${KMA-URL}")
    private String url;

    // 서울 종로 기준 좌표
    private static final int NX = 60;
    private static final int NY = 127;
    private static final int DATA_RELEASE_MINUTE = 40;

    public WeatherResponse getWeatherInfo() {

        LocalDateTime now = LocalDateTime.now();
        if (now.getMinute() < DATA_RELEASE_MINUTE) {
            now = now.minusHours(1);
        }

        String currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("HH00"));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("serviceKey", apiKey);
        params.add("pageNo", "1");
        params.add("numOfRows", "1");
        params.add("dataType", "JSON");
        params.add("base_date", currentDate);
        params.add("base_time", currentTime);
        params.add("nx", String.valueOf(NX));
        params.add("ny", String.valueOf(NY));

        String uri = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParams(params)
                .build()
                .toUriString();


        ResponseEntity<KmaWeatherApiResponse> response = restTemplate.getForEntity(uri, KmaWeatherApiResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("기상청 응답 상태 오류: {}", response.getStatusCode());
            throw new RuntimeException("기상청 날씨 조회 실패: " + response.getStatusCode());
        }

        KmaWeatherApiResponse api = response.getBody();
        if (api.response() == null || api.response().body() == null || api.response().body().items() == null
                || api.response().body().items().item() == null || api.response().body().items().item().isEmpty()) {
            log.error("기상청 응답 데이터가 비어있음: {}", api);
            throw new RuntimeException("기상청 응답 데이터가 비어있습니다.");
        }

        KmaWeatherApiResponse.Item first = api.response().body().items().item().get(0);
        return new WeatherResponse(first.category(), first.obsrValue());
    }

}
