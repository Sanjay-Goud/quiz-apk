package com.quiz.app.repository;

import com.quiz.app.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUserId(Long userId);

    List<QuizResult> findByQuizId(Long quizId);

    @Query("SELECT AVG(r.score * 100.0 / r.totalQuestions) FROM QuizResult r " +
            "WHERE r.userId = :userId")
    Double getAverageScorePercentage(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM QuizResult r WHERE r.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(r.totalQuestions) FROM QuizResult r WHERE r.userId = :userId")
    Long getTotalQuestionsAttempted(@Param("userId") Long userId);
}