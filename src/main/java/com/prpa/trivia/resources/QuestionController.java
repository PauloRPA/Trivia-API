package com.prpa.trivia.resources;

import com.prpa.trivia.model.trivia.Question;
import com.prpa.trivia.model.dto.QuestionDTO;
import com.prpa.trivia.model.exceptions.ResourceAlreadyExistException;
import com.prpa.trivia.model.exceptions.SpecificResourceNotFoundException;
import com.prpa.trivia.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Busca uma questão por ID (UUID).", method = "GET")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Questão com id especificado encontrada."),
            @ApiResponse(responseCode = "400", description = "Questão com id especificado não encontrada.")
    })
    @GetMapping(value = QUESTION_PATH + "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Question> getQuestion(@PathVariable("id") UUID id) {
        Question found = questionService.findById(id).orElseThrow(() ->
                new SpecificResourceNotFoundException("id", String.valueOf(id)));

        return ResponseEntity.ok(found);
    }

    @Operation(summary = "Busca questão dentro do offset e limit.", method = "GET")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Questões retornadas."),
    })
    @GetMapping(value = QUESTION_PATH, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Question>> getQuestion(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        offset = offset < 0 ? DEFAULT_OFFSET : offset;
        limit = limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);

        List<Question> found = questionService.findAll(offset, limit);

        return ResponseEntity.ok(found);
    }

    @Operation(summary = "Insere uma nova questão.", method = "POST")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Questão criada."),
            @ApiResponse(responseCode = "409", description = "Questão com enunciado especificado já existe."),
            @ApiResponse(responseCode = "400", description = "Questão inválida.")
    })
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

    @Operation(summary = "Altera uma questão por ID (UUID).", method = "PUT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Questão alterada com sucesso."),
            @ApiResponse(responseCode = "409", description = "Questão com enunciado especificado já existe."),
            @ApiResponse(responseCode = "400", description = "Questão inválida ou ID não encontrado.")
    })
    @PutMapping(value = QUESTION_PATH + "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Question> postQuestion(
            @PathVariable("id") UUID id,
            @Valid @RequestBody QuestionDTO newQuestion) {

        if (questionService.existsByStatementAndDifferentId(id, newQuestion.getStatement()))
            throw new ResourceAlreadyExistException("statement", newQuestion.getStatement());

        if (!questionService.existsById(id))
            throw new SpecificResourceNotFoundException("id", String.valueOf(id));

        Question updated = questionService.update(id, newQuestion);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Remove uma questão por ID (UUID).", method = "DELETE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Questão removida com sucesso."),
            @ApiResponse(responseCode = "400", description = "Questão com id especificado não encontrada.")
    })
    @DeleteMapping(QUESTION_PATH + "/{id}")
    public ResponseEntity<Void> postQuestion(@PathVariable("id") UUID id) {

        if (!questionService.existsById(id))
            throw new SpecificResourceNotFoundException("id", String.valueOf(id));

        questionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}