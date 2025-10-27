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
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public long getQuestionCount(Long categoryId) {
        return questionRepository.findByCategoryId(categoryId).size();
    }

    public long getQuizCount(Long categoryId) {
        return quizRepository.findByCategoryId(categoryId).size();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public Category addCategory(String name) {
        // Check if category already exists
        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException("Category already exists: " + name);
        }

        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }

        // Get counts for logging/confirmation
        List<Question> associatedQuestions = questionRepository.findByCategoryId(id);
        List<Quiz> associatedQuizzes = quizRepository.findByCategoryId(id);

        System.out.println("Deleting category " + id + " with " +
                associatedQuestions.size() + " questions and " +
                associatedQuizzes.size() + " quizzes");

        // Step 1: Delete all quizzes in this category
        // This automatically removes entries from quiz_questions table due to cascade
        for (Quiz quiz : associatedQuizzes) {
            quizRepository.deleteById(quiz.getId());
        }

        // Step 2: Delete all questions in this category
        // Now safe because they're not referenced by any quiz
        for (Question question : associatedQuestions) {
            questionRepository.deleteById(question.getId());
        }

        // Step 3: Delete the category itself
        categoryRepository.deleteById(id);

        System.out.println("Category " + id + " deleted successfully");
    }

    @Transactional
    public void deleteCategoryWithWarning(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }

        List<Question> associatedQuestions = questionRepository.findByCategoryId(id);
        List<Quiz> associatedQuizzes = quizRepository.findByCategoryId(id);

        if (!associatedQuestions.isEmpty() || !associatedQuizzes.isEmpty()) {
            throw new RuntimeException(
                    "Cannot delete category. It has " + associatedQuestions.size() +
                            " associated question(s) and " + associatedQuizzes.size() +
                            " associated quiz(zes). Please delete them first."
            );
        }

        categoryRepository.deleteById(id);
    }
}