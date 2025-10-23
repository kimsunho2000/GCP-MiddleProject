package com.gcp.assignment.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 음악 추천 중 발생하는 비즈니스 로직 예외 처리
     */
    @ExceptionHandler(MusicRecommendationException.class)
    public String handleMusicRecommendationException(MusicRecommendationException e, RedirectAttributes redirectAttributes) {
        log.error("음악 추천 중 오류 발생: {}", e.getMessage(), e);
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/";
    }

    /**
     * Bean Validation 예외 처리
     */
    @ExceptionHandler(BindException.class)
    public String handleBindException(BindException e, Model model) {
        log.warn("입력값 검증 실패: {}", e.getMessage());
        model.addAttribute("errorMessage", "입력값이 올바르지 않습니다. 다시 확인해주세요.");
        return "music-form";
    }

    /**
     * 일반적인 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e, Model model) {
        log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
        model.addAttribute("errorMessage", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        return "error";
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, RedirectAttributes redirectAttributes) {
        log.warn("잘못된 인자: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/";
    }
}


