package com.prpa.trivia.unit.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prpa.trivia.model.Category;
import com.prpa.trivia.model.Difficulty;
import com.prpa.trivia.model.Question;
import com.prpa.trivia.model.Type;
import com.prpa.trivia.model.dto.CategoryDTO;
import com.prpa.trivia.model.dto.QuestionDTO;
import com.prpa.trivia.model.exceptions.FieldReason;
import com.prpa.trivia.resources.QuestionController;
import com.prpa.trivia.service.QuestionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@WebMvcTest(QuestionController.class)
public class QuestionControllerTest {

    private static final Integer DEFAULT_LIMIT = QuestionController.DEFAULT_LIMIT;
    private static final Integer DEFAULT_OFFSET = QuestionController.DEFAULT_OFFSET;

    private static final String QUESTION_PATH = QuestionController.QUESTION_PATH;
    public static final UriComponentsBuilder QUESTION_ID_PATH = fromPath(QUESTION_PATH).path("/{id}");

    public static final UUID QUESTION_ID = UUID.randomUUID();
    public static final UUID QUESTION_INVALID_ID = UUID.randomUUID();
    public static final String QUESTION_STATEMENT = "Como consertar um carro?";
    public static final List<String> QUESTION_ALTERNATIVES = List.of("Alternativa A", "Alternativa B", "Alternativa C", "Alternativa D");
    public static final Type QUESTION_TYPE = Type.MULTIPLE;
    public static final Difficulty QUESTION_DIFFICULTY = Difficulty.MEDIUM;
    public static final Category QUESTION_CATEGORY = new Category(1L, "Art");
    public static final int QUESTION_ANSWER_INDEX = 0;

    private static Question QUESTION;

    private static ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private QuestionService questionService;

    @BeforeAll
    public static void setupAll() {
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void setup() {
        QUESTION = Question.builder()
                .id(QUESTION_ID)
                .statement(QUESTION_STATEMENT)
                .alternatives(QUESTION_ALTERNATIVES)
                .type(QUESTION_TYPE)
                .difficulty(QUESTION_DIFFICULTY)
                .category(List.of(QUESTION_CATEGORY))
                .correctIndex(QUESTION_ANSWER_INDEX)
                .build();
    }

    // ***************
    // GET /question/{id}
    // ***************

    @Test
    @DisplayName("Quando GET /question/{id} com ID válido deve retornar o item corretamente 200 OK")
    public void whenGETQuestionWithValidIDShouldReturn200OK() throws Exception {

        given(questionService.findById(eq(QUESTION_ID)))
                .willReturn(Optional.of(QUESTION));

        URI getURI = QUESTION_ID_PATH.build(QUESTION_ID);
        mockMvc.perform(get(getURI)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", equalTo(QUESTION_ID.toString())))
                .andExpect(jsonPath("$.category[0].id", equalTo(QUESTION_CATEGORY.getId().intValue())))
                .andExpect(jsonPath("$.category[0].name", equalTo(QUESTION_CATEGORY.getName())))
                .andExpect(jsonPath("$.difficulty", equalTo(QUESTION_DIFFICULTY.name())))
                .andExpect(jsonPath("$.type", equalTo(QUESTION_TYPE.name())))
                .andExpect(jsonPath("$.statement", equalTo(QUESTION_STATEMENT)))
                .andExpect(jsonPath("$.alternatives", equalTo(QUESTION_ALTERNATIVES)))
                .andExpect(jsonPath("$.correctIndex", equalTo(QUESTION_ANSWER_INDEX)));
    }

    @Test
    @DisplayName("Quando GET /question/{id} com ID que não existe deve retornar 400 BAD_REQUEST")
    public void whenGETQuestionWithIDThatDoesNotExistShouldReturn400BAD_REQUEST() throws Exception {
        String expectedDetail = message("error.resource.notfound.message", "id");

        given(questionService.findById(eq(QUESTION_INVALID_ID)))
                .willReturn(Optional.empty());

        URI getURI = QUESTION_ID_PATH.build(QUESTION_INVALID_ID);
        mockMvc.perform(get(getURI)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", equalTo(message("error.resource.notfound.title"))))
                .andExpect(jsonPath("$.detail", equalTo(expectedDetail)))
                .andExpect(jsonPath("$.instance", equalTo(QUESTION_PATH + "/" + QUESTION_INVALID_ID)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andDo(print());
    }

    @Test
    @DisplayName("Quando GET /question/{id} com ID com formato incorreto deve retornar 400 BAD_REQUEST")
    public void whenGETQuestionWithMalformedIDShouldReturn400BAD_REQUEST() throws Exception {
        String expectedTitle = "Bad Request";
        String malformedUUID = "malformedUUID";
        String expectedDetail = "Failed to convert '%s' with value: '%s'".formatted("id", malformedUUID);

        URI getURI = QUESTION_ID_PATH.build(malformedUUID);
        mockMvc.perform(get(getURI)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", equalTo(expectedTitle)))
                .andExpect(jsonPath("$.detail", equalTo(expectedDetail)))
                .andExpect(jsonPath("$.instance", equalTo(QUESTION_PATH + "/" + malformedUUID)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andDo(print());
    }

    // ***************
    // GET /question
    // ***************

    @Test
    @DisplayName("Quando GET /question sem parâmetros deve retornar página com DEFAULT_LIMIT e DEFAULT_OFFSET")
    public void whenGETQuestionWithNoParamShouldReturn200OKDefaultLimitAndOffset() throws Exception {
        final int OFFSET = DEFAULT_OFFSET, LIMIT = DEFAULT_LIMIT;

        List<Question> question = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            question.add(questionForStatement("Question[%d]".formatted(i)));
        }

        Question firstOffsetValue = question.get(OFFSET);
        given(questionService.findAll(eq(OFFSET), eq(LIMIT)))
                .willReturn(question.subList(OFFSET, OFFSET + LIMIT));

        mockMvc.perform(get(QUESTION_PATH)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$", hasSize(LIMIT)))
                .andExpect(jsonPath("$[0].id", equalTo(firstOffsetValue.getId().toString())))
                .andExpect(jsonPath("$[0].category[0].id", equalTo(QUESTION_CATEGORY.getId().intValue())))
                .andExpect(jsonPath("$[0].category[0].name", equalTo(QUESTION_CATEGORY.getName())))
                .andExpect(jsonPath("$[0].difficulty", equalTo(QUESTION_DIFFICULTY.name())))
                .andExpect(jsonPath("$[0].type", equalTo(QUESTION_TYPE.name())))
                .andExpect(jsonPath("$[0].statement", equalTo(firstOffsetValue.getStatement())))
                .andExpect(jsonPath("$[0].alternatives", equalTo(QUESTION_ALTERNATIVES)))
                .andExpect(jsonPath("$[0].correctIndex", equalTo(QUESTION_ANSWER_INDEX)));
    }

    @Test
    @DisplayName("Quando GET /question com limit e offset deve retornar página com limit e offset corretos")
    public void whenGETQuestionWithLimitOffsetParamShouldReturn200OKWithLimitAndOffset() throws Exception {
        final int OFFSET = 20, LIMIT = 10;

        List<Question> question = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            question.add(questionForStatement("Question[%d]".formatted(i)));
        }

        Question firstOffsetValue = question.get(OFFSET);
        given(questionService.findAll(eq(OFFSET), eq(LIMIT)))
                .willReturn(question.subList(OFFSET, OFFSET + LIMIT));

        mockMvc.perform(get(QUESTION_PATH)
                        .accept(APPLICATION_JSON)
                        .param("offset", String.valueOf(OFFSET))
                        .param("limit", String.valueOf(LIMIT)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$", hasSize(LIMIT)))
                .andExpect(jsonPath("$[0].id", equalTo(firstOffsetValue.getId().toString())))
                .andExpect(jsonPath("$[0].category[0].id", equalTo(QUESTION_CATEGORY.getId().intValue())))
                .andExpect(jsonPath("$[0].category[0].name", equalTo(QUESTION_CATEGORY.getName())))
                .andExpect(jsonPath("$[0].difficulty", equalTo(QUESTION_DIFFICULTY.name())))
                .andExpect(jsonPath("$[0].type", equalTo(QUESTION_TYPE.name())))
                .andExpect(jsonPath("$[0].statement", equalTo(firstOffsetValue.getStatement())))
                .andExpect(jsonPath("$[0].alternatives", equalTo(QUESTION_ALTERNATIVES)))
                .andExpect(jsonPath("$[0].correctIndex", equalTo(QUESTION_ANSWER_INDEX)));
    }

    @Test
    @DisplayName("Quando GET /question com parâmetros inválidos deve retornar com DEFAULT_LIMIT e DEFAULT_OFFSET")
    public void whenGETQuestionWithInvalidParamShouldReturn200OKDefaultLimitAndOffset() throws Exception {
        final int INVALID_OFFSET = -DEFAULT_OFFSET, INVALID_LIMIT = -DEFAULT_LIMIT;

        List<Question> question = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            question.add(questionForStatement("Question[%d]".formatted(i)));
        }

        Question firstOffsetValue = question.get(DEFAULT_OFFSET);
        given(questionService.findAll(DEFAULT_OFFSET, DEFAULT_LIMIT))
                .willReturn(question.subList(DEFAULT_OFFSET, DEFAULT_LIMIT));

        mockMvc.perform(get(QUESTION_PATH)
                        .accept(APPLICATION_JSON)
                        .param("offset", String.valueOf(INVALID_OFFSET))
                        .param("limit", String.valueOf(INVALID_LIMIT)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$", hasSize(DEFAULT_LIMIT)))
                .andExpect(jsonPath("$[0].id", equalTo(firstOffsetValue.getId().toString())))
                .andExpect(jsonPath("$[0].category[0].id", equalTo(QUESTION_CATEGORY.getId().intValue())))
                .andExpect(jsonPath("$[0].category[0].name", equalTo(QUESTION_CATEGORY.getName())))
                .andExpect(jsonPath("$[0].difficulty", equalTo(QUESTION_DIFFICULTY.name())))
                .andExpect(jsonPath("$[0].type", equalTo(QUESTION_TYPE.name())))
                .andExpect(jsonPath("$[0].statement", equalTo(firstOffsetValue.getStatement())))
                .andExpect(jsonPath("$[0].alternatives", equalTo(QUESTION_ALTERNATIVES)))
                .andExpect(jsonPath("$[0].correctIndex", equalTo(QUESTION_ANSWER_INDEX)))
                .andDo(print());

    }

    // ***************
    // POST /question
    // ***************

    @Test
    @DisplayName("Quando POST /question com o nome de uma questão deve 201 CREATED")
    public void whenPOSTQuestionCorrectlyShouldReturn201CREATED() throws Exception {
        String questionStatement = "Question statement";

        QuestionDTO newQuestion = questionDTOForStatement(questionStatement);
        Question expectedQuestion = questionForStatement(questionStatement);

        given(questionService.save(any())).willReturn(expectedQuestion);

        String expectedLocationURI = fromPath(QUESTION_PATH)
                .path("/{id}")
                .build(expectedQuestion.getId()).toString();

        mockMvc.perform(post(QUESTION_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuestion)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andExpect(header().string("location", expectedLocationURI))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", equalTo(expectedQuestion.getId().toString())))
                .andExpect(jsonPath("$.category[0].id", equalTo(QUESTION_CATEGORY.getId().intValue())))
                .andExpect(jsonPath("$.category[0].name", equalTo(QUESTION_CATEGORY.getName())))
                .andExpect(jsonPath("$.difficulty", equalTo(QUESTION_DIFFICULTY.name())))
                .andExpect(jsonPath("$.type", equalTo(QUESTION_TYPE.name())))
                .andExpect(jsonPath("$.statement", equalTo(expectedQuestion.getStatement())))
                .andExpect(jsonPath("$.alternatives", equalTo(QUESTION_ALTERNATIVES)))
                .andExpect(jsonPath("$.correctIndex", equalTo(QUESTION_ANSWER_INDEX)))
                .andDo(print());
    }

    @Test
    @DisplayName("Quando POST /question com um enunciado que já existe deve 409 CONFLICT")
    public void whenPOSTQuestionThatAlreadyExistShouldReturn409CONFLICT() throws Exception {
        String questionStatement = "Statement";
        QuestionDTO newQuestion = questionDTOForStatement(questionStatement);
        String expectedDetail = message("error.resource.exists.message", "statement");

        given(questionService.existsByStatement(any())).willReturn(true);

        mockMvc.perform(post(QUESTION_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuestion)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", equalTo(message("error.resource.exists.title"))))
                .andExpect(jsonPath("$.detail", equalTo(expectedDetail)))
                .andExpect(jsonPath("$.instance", equalTo(QUESTION_PATH)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.CONFLICT.value())))
                .andDo(print());
    }

    @Test
    @DisplayName("Quando POST /question com o enunciado de uma questão vazia 400 BAD_REQUEST")
    public void whenPOSTEmptyQuestionShouldReturn400BAD_REQUEST() throws Exception {
        QuestionDTO newQuestion = QuestionDTO.builder()
                .statement("")
                .correctIndex(-1)
                .build();
        Map<String, String> fieldAndMessage = Map.of(
                "statement", "error.question.empty.statement.message",
                "difficulty", "error.question.empty.difficulty.message",
                "type", "error.question.empty.type.message",
                "alternatives", "error.question.empty.alternatives.message",
                "correctIndex", "error.question.positive.correctIndex.message",
                "category", "error.question.empty.category.message"
        );
        List<FieldReason> expectedErrors = fieldAndMessage.entrySet().stream()
                .map((entry) -> new FieldReason(entry.getKey(), message(entry.getValue())))
                .toList();

        String[] fieldList = expectedErrors.stream().map(FieldReason::field).toArray(String[]::new);
        String[] reasonList = expectedErrors.stream().map(FieldReason::reason).toArray(String[]::new);
        mockMvc.perform(post(QUESTION_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuestion)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", equalTo("Bad Request")))
                .andExpect(jsonPath("$.detail", equalTo("Invalid request content.")))
                .andExpect(jsonPath("$.instance", equalTo(QUESTION_PATH)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.errors", hasSize(expectedErrors.size())))
                .andExpect(jsonPath("$.errors[*].field", containsInAnyOrder(fieldList)))
                .andExpect(jsonPath("$.errors[*].reason", containsInAnyOrder(reasonList)))
                .andDo(print());
    }

    // ***************
    // PUT /question/{id}
    // ***************

    @Test
    @DisplayName("Quando PUT /question/{id} com o nome válido 200 OK")
    public void whenPUTValidQuestionShouldReturn200OK() throws Exception {
        final UUID id = UUID.randomUUID();
        Question question = questionForStatement("Test");
        QuestionDTO newQuestion = questionDTOForStatement("Test2");
        System.out.println(QUESTION_DIFFICULTY.name());

        given(questionService.existsById(any())).willReturn(true);
        question.setStatement(newQuestion.getStatement());
        given(questionService.update(eq(id), eq(newQuestion))).willReturn(question);
        String putURI = QUESTION_ID_PATH.build(id).toString();
        mockMvc.perform(put(putURI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuestion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", equalTo(question.getId().toString())))
                .andExpect(jsonPath("$.category[0].id", equalTo(QUESTION_CATEGORY.getId().intValue())))
                .andExpect(jsonPath("$.category[0].name", equalTo(QUESTION_CATEGORY.getName())))
                .andExpect(jsonPath("$.difficulty", equalTo(QUESTION_DIFFICULTY.name())))
                .andExpect(jsonPath("$.type", equalTo(QUESTION_TYPE.name())))
                .andExpect(jsonPath("$.statement", equalTo(newQuestion.getStatement())))
                .andExpect(jsonPath("$.alternatives", equalTo(QUESTION_ALTERNATIVES)))
                .andExpect(jsonPath("$.correctIndex", equalTo(QUESTION_ANSWER_INDEX)))
                .andDo(print());
    }

    @Test
    @DisplayName("Quando PUT /question/{id} com id que não existe 400 BAD_REQUEST")
    public void whenPUTInvalidQuestionIDShouldReturn400BAD_REQUEST() throws Exception {
        final UUID invalidId = UUID.randomUUID();
        String expectedDetail = message("error.resource.notfound.message", "id");

        QuestionDTO newQuestion = questionDTOForStatement("Test");
        given(questionService.existsById(any())).willReturn(false);

        String putURI = QUESTION_ID_PATH.build(invalidId).toString();
        mockMvc.perform(put(putURI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuestion)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", equalTo(message("error.resource.notfound.title"))))
                .andExpect(jsonPath("$.detail", equalTo(expectedDetail)))
                .andExpect(jsonPath("$.instance", equalTo(QUESTION_PATH + "/" + invalidId)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andDo(print());
    }

    @Test
    @DisplayName("Quando PUT /question/{id} com ID com formato incorreto deve retornar 400 BAD_REQUEST")
    public void whenPutQuestionWithMalformedIDShouldReturn400BAD_REQUEST() throws Exception {
        String expectedTitle = "Bad Request";
        String malformedUUID = "malformedUUID";
        String expectedDetail = "Failed to convert '%s' with value: '%s'".formatted("id", malformedUUID);

        URI putURI = QUESTION_ID_PATH.build(malformedUUID);
        mockMvc.perform(get(putURI)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", equalTo(expectedTitle)))
                .andExpect(jsonPath("$.detail", equalTo(expectedDetail)))
                .andExpect(jsonPath("$.instance", equalTo(QUESTION_PATH + "/" + malformedUUID)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andDo(print());
    }


    @Test
    @DisplayName("Quando PUT /question/{id} com nome que já existe 409 CONFLICT")
    public void whenPUTQuestionWithQuestionAlreadyExistShouldReturn409CONFLICT() throws Exception {
        final UUID id = UUID.randomUUID();
        String questionStatement = "Test";
        String expectedDetail = message("error.resource.exists.message", "statement");

        QuestionDTO newQuestionAlreadyExists = questionDTOForStatement(questionStatement);
        given(questionService.existsByStatementAndDifferentId(eq(id), eq(newQuestionAlreadyExists.getStatement()))).willReturn(true);

        String putURI = QUESTION_ID_PATH.build(id).toString();
        mockMvc.perform(put(putURI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuestionAlreadyExists)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", equalTo(message("error.resource.exists.title"))))
                .andExpect(jsonPath("$.detail", equalTo(expectedDetail)))
                .andExpect(jsonPath("$.instance", equalTo(QUESTION_PATH + "/" + id)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.CONFLICT.value())))
                .andDo(print());
    }

    // ***************
    // DELETE /question/{id}
    // ***************

    @Test
    @DisplayName("Quando DELETE /question/{id} com o ID válido 204 NO_CONTENT")
    public void whenDELETEValidQuestionIDShouldReturn204NO_CONTENT() throws Exception {
        final UUID id = UUID.randomUUID();
        given(questionService.existsById(eq(id))).willReturn(true);

        String deleteURI = QUESTION_ID_PATH.build(id).toString();
        mockMvc.perform(delete(deleteURI)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("Quando DELETE /question/{id} com o ID inválido 400 BAD_REQUEST")
    public void whenDELETEInvalidQuestionIDShouldReturn400BAD_REQUEST() throws Exception {
        final UUID invalidId = UUID.randomUUID();
        String expectedDetail = message("error.resource.notfound.message", "id");
        given(questionService.existsById(eq(invalidId))).willReturn(false);

        String deleteURI = QUESTION_ID_PATH.build(invalidId).toString();
        mockMvc.perform(delete(deleteURI)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", equalTo(message("error.resource.notfound.title"))))
                .andExpect(jsonPath("$.detail", equalTo(expectedDetail)))
                .andExpect(jsonPath("$.instance", equalTo(QUESTION_PATH + "/" + invalidId)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andDo(print());
    }

    private Question questionForStatement(String statement) {
        return Question.builder()
                .id(UUID.randomUUID())
                .statement(statement)
                .alternatives(QUESTION_ALTERNATIVES)
                .type(QUESTION_TYPE)
                .difficulty(QUESTION_DIFFICULTY)
                .category(List.of(QUESTION_CATEGORY))
                .correctIndex(QUESTION_ANSWER_INDEX)
                .build();
    }

    private QuestionDTO questionDTOForStatement(String questionStatement) {
        return QuestionDTO.builder()
                .statement(questionStatement)
                .alternatives(QUESTION_ALTERNATIVES)
                .type(QUESTION_TYPE)
                .difficulty(QUESTION_DIFFICULTY)
                .category(List.of(new CategoryDTO(QUESTION_CATEGORY.getName())))
                .correctIndex(QUESTION_ANSWER_INDEX)
                .build();
    }

    @Autowired
    public void configureDelegatingMessageSource(DelegatingMessageSource delegatingMessageSource) {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("bundles.exceptions", "bundles.messages");
        delegatingMessageSource.setParentMessageSource(messageSource);
    }

    public String message(String message, Object... objects) {
        return messageSource.getMessage(message, objects, Locale.getDefault());
    }

}