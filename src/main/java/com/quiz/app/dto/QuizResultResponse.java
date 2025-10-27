package com.quiz.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizResultResponse {
    private Long resultId;
    private Integer score;
    private Integer totalQuestions;
    private Double percentage;
    private String message;
}