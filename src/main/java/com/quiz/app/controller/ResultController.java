package com.quiz.app.controller;

import com.quiz.app.dto.PerformanceStats;
import com.quiz.app.entity.QuizResult;
import com.quiz.app.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/result")
@CrossOrigin(origins = "*")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<QuizResult>> getAllResults() {
        return ResponseEntity.ok(resultService.getAllResults());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizResult>> getResultsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(resultService.getResultsByUserId(userId));
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizResult>> getResultsByQuizId(@PathVariable Long quizId) {
        return ResponseEntity.ok(resultService.getResultsByQuizId(quizId));
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getUserPerformanceStats(@PathVariable Long userId) {
        try {
            PerformanceStats stats = resultService.getUserPerformanceStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}