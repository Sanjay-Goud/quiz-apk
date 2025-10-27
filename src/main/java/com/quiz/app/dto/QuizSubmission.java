package com.quiz.app.dto;

import lombok.Data;

import java.util.Map;

@Data
public class QuizSubmission {
    private Long userId;
    private Long quizId;
    private Map<Long, String> answers; // questionId -> selectedOption
}