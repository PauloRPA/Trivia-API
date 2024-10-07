package com.prpa.trivia.resources;

import com.prpa.trivia.model.Question;
import com.prpa.trivia.model.dto.QuestionDTO;
import com.prpa.trivia.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping
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
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @GetMapping(value = QUESTION_PATH, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Question>> getQuestion(
            @RequestParam(defaultValue = "-1") int offset,
            @RequestParam(defaultValue = "-1") int limit) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @PostMapping(value = QUESTION_PATH, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Question> postQuestion(@Valid @RequestBody QuestionDTO newQuestion) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @PutMapping(value = QUESTION_PATH + "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Question> postQuestion(
            @PathVariable("id") UUID id,
            @Valid @RequestBody QuestionDTO newQuestion) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @DeleteMapping(QUESTION_PATH + "/{id}")
    public ResponseEntity<Void> postQuestion(@PathVariable("id") UUID id) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}