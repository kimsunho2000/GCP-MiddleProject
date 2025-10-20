package com.gcp.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MusicResponse {
    private List<Song> playlist;
    private String comment;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Song {
        private String title;
        private String artist;
    }
}
