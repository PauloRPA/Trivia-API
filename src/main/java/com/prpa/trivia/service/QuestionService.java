package com.prpa.trivia.service;

import com.prpa.trivia.model.Question;
import com.prpa.trivia.model.dto.QuestionDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuestionService {
    public Optional<Question> findById(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public List<Question> findAll(int offset, int limit) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean existsByStatement(String question) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public Question save(QuestionDTO newQuestion) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean existsById(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public Question update(UUID id, QuestionDTO newQuestion) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void delete(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
