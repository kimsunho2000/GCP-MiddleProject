package com.gcp.assignment.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MusicRequest {
    private String mood;
    private int numberOfSongs;
    private List<String> favoriteArtists;
    private List<String> favoriteGenres;
    private String story;
}
