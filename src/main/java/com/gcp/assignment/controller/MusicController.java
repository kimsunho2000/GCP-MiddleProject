package com.gcp.assignment.controller;

import com.gcp.assignment.dto.MusicRequest;
import com.gcp.assignment.dto.MusicResponse;
import com.gcp.assignment.service.MusicRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
public class MusicController {

    private final MusicRecommendationService musicRecommendationService;

    @GetMapping("/")
    public String showMusicForm(Model model) {
        MusicRequest musicRequest = new MusicRequest();
        musicRequest.setFavoriteArtists(new ArrayList<>());
        musicRequest.setFavoriteGenres(new ArrayList<>());
        model.addAttribute("musicRequest", musicRequest);
        return "music-form";
    }

    @PostMapping("/recommend")
    public String recommendMusic(@ModelAttribute MusicRequest musicRequest, Model model) {
        MusicResponse musicResponse = musicRecommendationService.getMusicRecommendation(musicRequest);
        model.addAttribute("musicResponse", musicResponse);
        return "music-result";
    }
}

