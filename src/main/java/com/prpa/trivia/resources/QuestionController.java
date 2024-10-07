package com.prpa.trivia.resources;

import com.prpa.trivia.model.Question;
import com.prpa.trivia.model.dto.QuestionDTO;
import com.prpa.trivia.model.exceptions.ResourceAlreadyExistException;
import com.prpa.trivia.model.exceptions.SpecificResourceNotFoundException;
import com.prpa.trivia.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class QuestionController {

    public static final String API = "/api/v1";
    public static final String QUESTION_PATH = API + "/question";

    public static final Integer DEFAULT_OFFSET = 0;
    public static final Integer DEFAULT_LIMIT = 10;

    public static final Integer MAX_LIMIT = 100;

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping(value = QUESTION_PATH + "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Question> getQuestion(@PathVariable("id") UUID id) {
        Question found = questionService.findById(id).orElseThrow(() ->
                new SpecificResourceNotFoundException("id"));

        return ResponseEntity.ok(found);
    }

    @GetMapping(value = QUESTION_PATH, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Question>> getQuestion(
            @RequestParam(defaultValue = "-1") int offset,
            @RequestParam(defaultValue = "-1") int limit) {
        offset = offset < 0 ? DEFAULT_OFFSET : offset;
        limit = limit < 0 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);

        List<Question> found = questionService.findAll(offset, limit);

        return ResponseEntity.ok(found);
    }

    @PostMapping(value = QUESTION_PATH, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Question> postQuestion(@Valid @RequestBody QuestionDTO newQuestion) {
        if (questionService.existsByStatement(newQuestion.getStatement())) {
            throw new ResourceAlreadyExistException("statement", newQuestion.getStatement());
        }

        Question created = questionService.save(newQuestion);
        URI locationURI = UriComponentsBuilder.fromPath(QUESTION_PATH)
                .path("/{id}")
                .build(created.getId());
        return ResponseEntity.created(locationURI).body(created);
    }

    @PutMapping(value = QUESTION_PATH + "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Question> postQuestion(
            @PathVariable("id") UUID id,
            @Valid @RequestBody QuestionDTO newQuestion) {

        if (questionService.existsByStatementAndDifferentId(id, newQuestion.getStatement()))
            throw new ResourceAlreadyExistException("statement", newQuestion.getStatement());

        if (!questionService.existsById(id))
            throw new SpecificResourceNotFoundException("id");

        Question updated = questionService.update(id, newQuestion);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(QUESTION_PATH + "/{id}")
    public ResponseEntity<Void> postQuestion(@PathVariable("id") UUID id) {

        if (!questionService.existsById(id))
            throw new SpecificResourceNotFoundException("id");

        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}