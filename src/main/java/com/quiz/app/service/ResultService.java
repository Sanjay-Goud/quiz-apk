package com.quiz.app.service;

import com.quiz.app.dto.PerformanceStats;
import com.quiz.app.entity.QuizResult;
import com.quiz.app.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultService {

    @Autowired
    private QuizResultRepository quizResultRepository;

    public List<QuizResult> getAllResults() {
        return quizResultRepository.findAll();
    }

    public List<QuizResult> getResultsByUserId(Long userId) {
        return quizResultRepository.findByUserId(userId);
    }

    public List<QuizResult> getResultsByQuizId(Long quizId) {
        return quizResultRepository.findByQuizId(quizId);
    }

    public PerformanceStats getUserPerformanceStats(Long userId) {
        Long totalQuizzes = quizResultRepository.countByUserId(userId);
        Long totalQuestions = quizResultRepository.getTotalQuestionsAttempted(userId);
        Double avgPercentage = quizResultRepository.getAverageScorePercentage(userId);

        // Handle null values
        if (totalQuizzes == null) totalQuizzes = 0L;
        if (totalQuestions == null) totalQuestions = 0L;
        if (avgPercentage == null) avgPercentage = 0.0;

        String performance = getPerformanceLevel(avgPercentage);

        return new PerformanceStats(
                totalQuizzes,
                totalQuestions,
                avgPercentage,
                performance
        );
    }

    private String getPerformanceLevel(double avgPercentage) {
        if (avgPercentage >= 85) {
            return "Excellent";
        } else if (avgPercentage >= 70) {
            return "Good";
        } else if (avgPercentage >= 50) {
            return "Average";
        } else {
            return "Needs Improvement";
        }
    }
}