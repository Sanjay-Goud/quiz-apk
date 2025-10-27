package com.quiz.app.service;

import com.quiz.app.dto.CustomQuizRequest;
import com.quiz.app.dto.QuizResultResponse;
import com.quiz.app.dto.QuizSubmission;
import com.quiz.app.entity.Category;
import com.quiz.app.entity.Question;
import com.quiz.app.entity.Quiz;
import com.quiz.app.entity.QuizResult;
import com.quiz.app.repository.CategoryRepository;
import com.quiz.app.repository.QuestionRepository;
import com.quiz.app.repository.QuizRepository;
import com.quiz.app.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public Quiz createQuiz(String title, Long categoryId,
                           Question.Difficulty difficulty,
                           String createdBy,
                           int numQuestions) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        List<Question> questions = questionService.getRandomQuestions(
                categoryId, difficulty, numQuestions
        );

        if (questions.isEmpty()) {
            throw new RuntimeException("No questions available for the selected criteria");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setCategory(category);
        quiz.setDifficulty(difficulty);
        quiz.setCreatedBy(createdBy);
        quiz.setQuestions(questions);

        return quizRepository.save(quiz);
    }

    public List<Question> createCustomQuiz(CustomQuizRequest request) {
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new RuntimeException("Category not found");
        }

        List<Question> questions = questionService.getRandomQuestions(
                request.getCategoryId(),
                request.getDifficulty(),
                request.getNumQuestions()
        );

        if (questions.isEmpty()) {
            throw new RuntimeException("No questions available for the selected criteria");
        }

        if (questions.size() < request.getNumQuestions()) {
            throw new RuntimeException(
                    "Only " + questions.size() + " questions available. Requested: " +
                            request.getNumQuestions()
            );
        }

        return questions;
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public List<Quiz> getQuizzesByCategory(Long categoryId) {
        return quizRepository.findByCategoryId(categoryId);
    }

    public QuizResultResponse submitQuiz(QuizSubmission submission) {
        Quiz quiz = getQuizById(submission.getQuizId());
        Map<Long, String> answers = submission.getAnswers();

        int score = 0;
        int totalQuestions = quiz.getQuestions().size();

        for (Question question : quiz.getQuestions()) {
            String userAnswer = answers.get(question.getId());
            if (userAnswer != null && userAnswer.equals(question.getCorrectOption())) {
                score++;
            }
        }

        QuizResult result = new QuizResult();
        result.setUserId(submission.getUserId());
        result.setQuizId(submission.getQuizId());
        result.setScore(score);
        result.setTotalQuestions(totalQuestions);
        result.setTimestamp(LocalDateTime.now());

        QuizResult savedResult = quizResultRepository.save(result);

        double percentage = (score * 100.0) / totalQuestions;
        String message = getPerformanceMessage(percentage);

        return new QuizResultResponse(
                savedResult.getId(),
                score,
                totalQuestions,
                percentage,
                message
        );
    }

    // NEW: Method to handle custom quiz submissions (without saving quiz)
    public QuizResultResponse submitCustomQuiz(QuizSubmission submission) {
        Map<Long, String> answers = submission.getAnswers();

        if (answers == null || answers.isEmpty()) {
            throw new RuntimeException("No answers provided");
        }

        // Fetch all questions from the submission
        List<Long> questionIds = answers.keySet().stream().toList();
        List<Question> questions = questionRepository.findAllById(questionIds);

        if (questions.isEmpty()) {
            throw new RuntimeException("Questions not found");
        }

        int score = 0;
        int totalQuestions = questions.size();

        // Calculate score
        for (Question question : questions) {
            String userAnswer = answers.get(question.getId());
            if (userAnswer != null && userAnswer.equals(question.getCorrectOption())) {
                score++;
            }
        }

        // Save result with quizId as null for custom quizzes
        QuizResult result = new QuizResult();
        result.setUserId(submission.getUserId());
        result.setQuizId(null);  // Custom quiz doesn't have a quiz ID
        result.setScore(score);
        result.setTotalQuestions(totalQuestions);
        result.setTimestamp(LocalDateTime.now());

        QuizResult savedResult = quizResultRepository.save(result);

        double percentage = (score * 100.0) / totalQuestions;
        String message = getPerformanceMessage(percentage);

        return new QuizResultResponse(
                savedResult.getId(),
                score,
                totalQuestions,
                percentage,
                message
        );
    }

    private String getPerformanceMessage(double percentage) {
        if (percentage >= 90) {
            return "Excellent! Outstanding performance!";
        } else if (percentage >= 75) {
            return "Great job! Well done!";
        } else if (percentage >= 60) {
            return "Good effort! Keep practicing!";
        } else if (percentage >= 40) {
            return "Not bad! Room for improvement!";
        } else {
            return "Keep trying! Practice makes perfect!";
        }
    }

    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new RuntimeException("Quiz not found with id: " + id);
        }
        quizRepository.deleteById(id);
    }
}