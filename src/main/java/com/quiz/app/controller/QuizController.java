package com.quiz.app.controller;

import com.quiz.app.dto.CustomQuizRequest;
import com.quiz.app.dto.QuizResultResponse;
import com.quiz.app.dto.QuizSubmission;
import com.quiz.app.entity.Question;
import com.quiz.app.entity.Quiz;
import com.quiz.app.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quiz")
@CrossOrigin(origins = "*")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createQuiz(@RequestBody Map<String, Object> request) {
        try {
            String title = (String) request.get("title");
            Long categoryId = Long.valueOf(request.get("categoryId").toString());
            Question.Difficulty difficulty =
                    Question.Difficulty.valueOf((String) request.get("difficulty"));
            String createdBy = (String) request.get("createdBy");
            int numQuestions = Integer.parseInt(request.get("numQuestions").toString());

            Quiz quiz = quizService.createQuiz(
                    title, categoryId, difficulty, createdBy, numQuestions
            );
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/custom")
    public ResponseEntity<?> createCustomQuiz(@Valid @RequestBody CustomQuizRequest request) {
        try {
            List<Question> questions = quizService.createCustomQuiz(request);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable Long id) {
        try {
            Quiz quiz = quizService.getQuizById(id);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Quiz>> getQuizzesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(quizService.getQuizzesByCategory(categoryId));
    }

    @PostMapping("/submit/{quizId}")
    public ResponseEntity<?> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizSubmission submission) {
        try {
            submission.setQuizId(quizId);
            QuizResultResponse result = quizService.submitQuiz(submission);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        try {
            quizService.deleteQuiz(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Quiz deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}