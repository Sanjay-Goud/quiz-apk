package com.quiz.app.dto;

import com.quiz.app.entity.Question;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomQuizRequest {
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Difficulty is required")
    private Question.Difficulty difficulty;

    @Min(value = 1, message = "Number of questions must be at least 1")
    @NotNull(message = "Number of questions is required")
    private Integer numQuestions;
}