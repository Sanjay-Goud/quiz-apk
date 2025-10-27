package com.quiz.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PerformanceStats {
    private Long totalQuizzesTaken;
    private Long totalQuestionsAttempted;
    private Double averageScorePercentage;
    private String performance; // Excellent, Good, Average, Needs Improvement
}