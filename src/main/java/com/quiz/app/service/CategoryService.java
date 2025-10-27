//package com.quiz.app.service;
//
//import com.quiz.app.entity.Category;
//import com.quiz.app.repository.CategoryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class CategoryService {
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    public List<Category> getAllCategories() {
//        return categoryRepository.findAll();
//    }
//
//    public Category getCategoryById(Long id) {
//        return categoryRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
//    }
//
//    public Category addCategory(String name) {
//        // Check if category already exists
//        if (categoryRepository.existsByName(name)) {
//            throw new RuntimeException("Category already exists: " + name);
//        }
//
//        Category category = new Category();
//        category.setName(name);
//        return categoryRepository.save(category);
//    }
//
//    public void deleteCategory(Long id) {
//        if (!categoryRepository.existsById(id)) {
//            throw new RuntimeException("Category not found with id: " + id);
//        }
//        categoryRepository.deleteById(id);
//    }
//}


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

        // Check if category has associated questions
        List<Question> associatedQuestions = questionRepository.findByCategoryId(id);

        if (!associatedQuestions.isEmpty()) {
            throw new RuntimeException(
                    "Cannot delete category. It has " + associatedQuestions.size() +
                            " associated question(s). Please delete the questions first."
            );
        }

        // Check if category has associated quizzes
        List<Quiz> associatedQuizzes = quizRepository.findByCategoryId(id);

        if (!associatedQuizzes.isEmpty()) {
            throw new RuntimeException(
                    "Cannot delete category. It has " + associatedQuizzes.size() +
                            " associated quiz(zes). Please delete the quizzes first."
            );
        }

        categoryRepository.deleteById(id);
    }
}