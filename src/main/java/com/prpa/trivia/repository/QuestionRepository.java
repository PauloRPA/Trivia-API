package com.prpa.trivia.repository;

import com.prpa.trivia.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

    boolean existsByStatement(String statement);

}
