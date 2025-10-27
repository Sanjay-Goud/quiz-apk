package com.quiz.app.service;

import com.quiz.app.entity.Category;
import com.quiz.app.entity.Question;
import com.quiz.app.entity.Quiz;
import com.quiz.app.repository.CategoryRepository;
import com.quiz.app.repository.QuestionRepository;
import com.quiz.app.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuizRepository quizRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
    }

    public List<Question> getQuestionsByCategory(Long categoryId) {
        return questionRepository.findByCategoryId(categoryId);
    }

    public List<Question> getQuestionsByDifficulty(Question.Difficulty difficulty) {
        return questionRepository.findByDifficulty(difficulty);
    }

    public List<Question> getQuestionsByCategoryAndDifficulty(
            Long categoryId,
            Question.Difficulty difficulty) {
        return questionRepository.findByCategoryIdAndDifficulty(categoryId, difficulty);
    }

    public Question addQuestion(Question question) {
        // Validate category exists
        Category category = categoryRepository.findById(question.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        question.setCategory(category);
        return questionRepository.save(question);
    }

    public Question updateQuestion(Long id, Question updatedQuestion) {
        Question existingQuestion = getQuestionById(id);

        existingQuestion.setQuestionTitle(updatedQuestion.getQuestionTitle());
        existingQuestion.setOption1(updatedQuestion.getOption1());
        existingQuestion.setOption2(updatedQuestion.getOption2());
        existingQuestion.setOption3(updatedQuestion.getOption3());
        existingQuestion.setOption4(updatedQuestion.getOption4());
        existingQuestion.setCorrectOption(updatedQuestion.getCorrectOption());
        existingQuestion.setDifficulty(updatedQuestion.getDifficulty());

        if (updatedQuestion.getCategory() != null) {
            Category category = categoryRepository.findById(updatedQuestion.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existingQuestion.setCategory(category);
        }

        return questionRepository.save(existingQuestion);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new RuntimeException("Question not found with id: " + id);
        }

        // Remove question from all quizzes that contain it
        List<Quiz> quizzes = quizRepository.findAll();
        boolean removedFromQuizzes = false;

        for (Quiz quiz : quizzes) {
            if (quiz.getQuestions().removeIf(q -> q.getId().equals(id))) {
                quizRepository.save(quiz);
                removedFromQuizzes = true;
            }
        }

        // Now safe to delete the question
        questionRepository.deleteById(id);
    }

    public List<Question> getRandomQuestions(Long categoryId,
                                             Question.Difficulty difficulty,
                                             int numQuestions) {
        return questionRepository.findRandomQuestions(
                categoryId,
                difficulty.name(),
                numQuestions
        );
    }
}