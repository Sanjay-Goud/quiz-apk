package com.quiz.app.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Simplified QuizQuestion Entity
 * This approach uses ManyToMany directly in Quiz.java
 * So this separate entity file is OPTIONAL
 */
@Entity
@Table(name = "quiz_questions")
@Data
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;
}
