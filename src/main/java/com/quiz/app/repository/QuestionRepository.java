package com.quiz.app.repository;

import com.quiz.app.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCategoryId(Long categoryId);

    List<Question> findByDifficulty(Question.Difficulty difficulty);

    List<Question> findByCategoryIdAndDifficulty(
            Long categoryId,
            Question.Difficulty difficulty
    );

    @Query(value = "SELECT * FROM questions WHERE category_id = :categoryId " +
            "AND difficulty = :difficulty ORDER BY RANDOM() LIMIT :limit",
            nativeQuery = true)
    List<Question> findRandomQuestions(
            @Param("categoryId") Long categoryId,
            @Param("difficulty") String difficulty,
            @Param("limit") int limit
    );
}