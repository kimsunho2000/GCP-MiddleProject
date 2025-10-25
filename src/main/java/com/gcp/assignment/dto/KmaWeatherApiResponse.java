package com.gcp.assignment.dto;

import java.util.List;

/**
 * KMA(기상청) 초단기실황 응답 JSON 구조를 그대로 매핑하는 DTO
 */
public record KmaWeatherApiResponse(Response response) {

    public static record Response(Header header, Body body) {}

    public static record Header(String resultCode, String resultMsg) {}

    public static record Body(String dataType, Items items, int pageNo, int numOfRows, int totalCount) {}

    public static record Items(List<Item> item) {}

    public static record Item(
            String baseDate,
            String baseTime,
            String category,
            int nx,
            int ny,
            String obsrValue
    ) {}
}

