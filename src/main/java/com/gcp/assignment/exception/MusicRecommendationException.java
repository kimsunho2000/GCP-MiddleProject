package com.gcp.assignment.exception;

public class MusicRecommendationException extends RuntimeException {
    public MusicRecommendationException(String message) {
        super(message);
    }

    public MusicRecommendationException(String message, Throwable cause) {
        super(message, cause);
    }
}