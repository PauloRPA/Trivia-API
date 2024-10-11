package com.prpa.trivia.service;

import com.prpa.trivia.model.Category;
import com.prpa.trivia.model.Question;
import com.prpa.trivia.model.dto.QuestionDTO;
import com.prpa.trivia.model.exceptions.SpecificResourceNotFoundException;
import com.prpa.trivia.repository.QuestionRepository;
import com.prpa.trivia.resources.OffsetPageRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryService categoryService;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, CategoryService categoryService) {
        this.questionRepository = questionRepository;
        this.categoryService = categoryService;
    }

    public Optional<Question> findById(UUID id) {
        return questionRepository.findById(id);
    }

    public List<Question> findAll(int offset, int limit) {
        if (offset < 0) throw new IllegalArgumentException("Offset must be greater than 1.");
        if (limit < 1) throw new IllegalArgumentException("Limit must be greater than 1.");

        Pageable page = OffsetPageRequest.of(offset, limit);
        return questionRepository.findAll(page).toList();
    }

    public boolean existsByStatement(String statement) {
        return questionRepository.existsByStatement(statement);
    }

    public Question save(@Valid QuestionDTO newQuestion) {
        List<Category> categories = categoryService.findOrSaveAll(newQuestion.getCategory());

        Question questionToBeSaved = Question.builder()
                .category(categories)
                .difficulty(newQuestion.getDifficulty())
                .type(newQuestion.getType())
                .statement(newQuestion.getStatement().trim())
                .alternatives(newQuestion.getAlternatives())
                .correctIndex(newQuestion.getCorrectIndex())
                .build();

        return questionRepository.save(questionToBeSaved);
    }

    public boolean existsById(UUID id) {
        return questionRepository.existsById(id);
    }

    public Question update(UUID id, QuestionDTO newQuestion) {
        Question found = questionRepository.findById(id)
                .orElseThrow(() -> new SpecificResourceNotFoundException("id", String.valueOf(id)));

        List<Category> categories = categoryService.findOrSaveAll(newQuestion.getCategory());

        found.setCategory(categories.isEmpty() ? found.getCategory() : categories);
        found.setDifficulty(Objects.requireNonNullElse(newQuestion.getDifficulty(), found.getDifficulty()));
        found.setType(Objects.requireNonNullElse(newQuestion.getType(), found.getType()));
        found.setStatement(requireNonEmptyElse(newQuestion.getStatement(), found.getStatement()));
        found.setAlternatives(Objects.requireNonNullElse(newQuestion.getAlternatives(), found.getAlternatives()));
        found.setCorrectIndex(positiveOrZeroElse(newQuestion.getCorrectIndex(), found.getCorrectIndex()));

        return questionRepository.save(found);
    }

    private Integer positiveOrZeroElse(Integer index, Integer defaultValue) {
        if (index == null) return defaultValue;
        return index >= 0 ? index : defaultValue;
    }

    private String requireNonEmptyElse(String string, String defaultValue) {
        return string == null || string.isBlank() ? defaultValue : string;
    }

    public void delete(UUID id) {
        questionRepository.deleteById(id);
    }

    public boolean existsByStatementAndDifferentId(UUID id, String statement) {
        final boolean isSameEntity = questionRepository.findById(id)
                .orElseThrow(() -> new SpecificResourceNotFoundException("id", String.valueOf(id)))
                .getStatement().equals(statement);
        if (isSameEntity) return false;
        return questionRepository.existsByStatement(statement);
    }
}
