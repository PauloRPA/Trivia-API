package com.prpa.trivia.unit.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prpa.trivia.model.trivia.Category;
import com.prpa.trivia.model.dto.CategoryDTO;
import com.prpa.trivia.model.exceptions.FieldReason;
import com.prpa.trivia.resources.CategoryController;
import com.prpa.trivia.service.CategoryService;
import org.junit.jupiter.api.BeforeAll;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.prpa.trivia.resources.CategoryController.API;
import static com.prpa.trivia.resources.CategoryController.CATEGORIES;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    private final Integer DEFAULT_LIMIT = CategoryController.DEFAULT_LIMIT;
    private final Integer DEFAULT_OFFSET = CategoryController.DEFAULT_OFFSET;

    private static ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private CategoryService categoryService;

    @BeforeAll
    public static void setupAll() {
        objectMapper = new ObjectMapper();
    }

    // ***************
    // GET /categories/{id}
    // ***************

    @Test
    @DisplayName("Quando GET /categories/{id} com ID válido deve retornar o item corretamente 200 OK")
    public void whenGETCategoryWithValidIDShouldReturn200OK() throws Exception {
        final Long id = 1L;
        String categoryName = "Art";

        given(categoryService.findById(eq(id)))
                .willReturn(Optional.of(new Category(id, categoryName)));

        URI getURI = UriComponentsBuilder.fromPath(CATEGORIES).path("/{id}").build(id);
        mockMvc.perform(get(getURI)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", equalTo(id.intValue())))
                .andExpect(jsonPath("$.name", equalTo(categoryName)));
    }

    @Test
    @DisplayName("Quando GET /categories/{id} com ID inválido deve retornar 400 BAD_REQUEST")
    public void whenGETCategoryWithInvalidIDShouldReturn400BAD_REQUEST() throws Exception {
        final long invalidId = 1L;
        String expectedDetail = message("error.resource.notfound.message", "id");

        given(categoryService.findById(eq(invalidId)))
                .willReturn(Optional.empty());

        URI getURI = UriComponentsBuilder.fromPath(CATEGORIES).path("/{id}").build(invalidId);
        mockMvc.perform(get(getURI)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", equalTo(message("error.resource.notfound.title"))))
                .andExpect(jsonPath("$.detail", equalTo(expectedDetail)))
                .andExpect(jsonPath("$.instance", equalTo(API + "/categories/" + invalidId)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andDo(print());
    }



    // ***************
    // GET /categories
    // ***************

    @Test
    @DisplayName("Quando GET /categories sem parâmetros deve retornar página com DEFAULT_LIMIT e DEFAULT_OFFSET")
    public void whenGETCategoriesWithNoParamShouldReturn200OKDefaultLimitAndOffset() throws Exception {
        final int OFFSET = DEFAULT_OFFSET, LIMIT = DEFAULT_LIMIT;

        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            categories.add(new Category((long) i, "Category[%d]".formatted(i)));
        }

        Category firstOffsetValue = categories.get(OFFSET);
        given(categoryService.findAll(eq(OFFSET), eq(LIMIT)))
                .willReturn(categories.subList(OFFSET, OFFSET + LIMIT));

        mockMvc.perform(get(CATEGORIES)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$", hasSize(LIMIT)))
                .andExpect(jsonPath("$[0].id", equalTo(firstOffsetValue.getId().intValue())))
                .andExpect(jsonPath("$[0].name", equalTo(firstOffsetValue.getName())));
    }

    @Test
    @DisplayName("Quando GET /categories com limit e offset deve retornar página com limit e offset corretos")
    public void whenGETCategoriesWithLimitOffsetParamShouldReturn200OKWithLimitAndOffset() throws Exception {
        final int OFFSET = 20, LIMIT = 10;

        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            categories.add(new Category((long) i, "Category[%d]".formatted(i)));
        }

        Category firstOffsetValue = categories.get(OFFSET);
        given(categoryService.findAll(eq(OFFSET), eq(LIMIT)))
                .willReturn(categories.subList(OFFSET, OFFSET + LIMIT));

        mockMvc.perform(get(API + "/categories")
                        .accept(APPLICATION_JSON)
                        .param("offset", String.valueOf(OFFSET))
                        .param("limit", String.valueOf(LIMIT)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$", hasSize(LIMIT)))
                .andExpect(jsonPath("$[0].id", equalTo(firstOffsetValue.getId().intValue())))
                .andExpect(jsonPath("$[0].name", equalTo(firstOffsetValue.getName())));
    }

    @Test
    @DisplayName("Quando GET /categories com parâmetros inválidos deve retornar com DEFAULT_LIMIT e DEFAULT_OFFSET")
    public void whenGETCategoriesWithInvalidParamShouldReturn200OKDefaultLimitAndOffset() throws Exception {
        final int INVALID_OFFSET = -DEFAULT_OFFSET, INVALID_LIMIT = -DEFAULT_LIMIT;

        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            categories.add(new Category((long) i, "Category[%d]".formatted(i)));
        }

        Category firstOffsetValue = categories.get(DEFAULT_OFFSET);
        given(categoryService.findAll(DEFAULT_OFFSET, DEFAULT_LIMIT))
                .willReturn(categories.subList(DEFAULT_OFFSET, DEFAULT_LIMIT));

        mockMvc.perform(get(CATEGORIES)
                        .accept(APPLICATION_JSON)
                        .param("offset", String.valueOf(INVALID_OFFSET))
                        .param("limit", String.valueOf(INVALID_LIMIT)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$", hasSize(DEFAULT_LIMIT)))
                .andExpect(jsonPath("$[0].id", equalTo(firstOffsetValue.getId().intValue())))
                .andExpect(jsonPath("$[0].name", equalTo(firstOffsetValue.getName())))
                .andDo(print());

    }

    // ***************
    // POST /categories
    // ***************

    @Test
    @DisplayName("Quando POST /categories com o nome de uma categoria deve 201 CREATED")
    public void whenPOSTCategoriesCorrectlyShouldReturn201CREATED() throws Exception {
        final long newCategoryId = 1L;

        CategoryDTO newCategory = new CategoryDTO("Art");
        Category expectedCategory = new Category(newCategoryId, "Art");

        given(categoryService.save(any())).willReturn(expectedCategory);

        String expectedLocationURI = UriComponentsBuilder.fromPath(CATEGORIES)
                .path("{id}")
                .build(newCategoryId).toString();

        mockMvc.perform(post(API + "/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andExpect(header().string("location", expectedLocationURI))
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", equalTo(expectedCategory.getId().intValue())))
                .andExpect(jsonPath("$.name", equalTo(expectedCategory.getName())))
                .andDo(print());
    }

    @Test
    @DisplayName("Quando POST /categories com o nome de uma categoria que ja existe deve 409 CONFLICT")
    public void whenPOSTCategoriesThatAlreadyExistShouldReturn409CONFLICT() throws Exception {
        String categoryName = "Art";
        CategoryDTO newCategory = new CategoryDTO(categoryName);
        String expectedDetail = message("error.resource.exists.message", "name");

        given(categoryService.existsByName(any())).willReturn(true);

        mockMvc.perform(post(API + "/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", equalTo(message("error.resource.exists.title"))))
                .andExpect(jsonPath("$.detail", equalTo(expectedDetail)))
                .andExpect(jsonPath("$.instance", equalTo(API + "/categories")))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.CONFLICT.value())))
                .andDo(print());
    }

    @Test
    @DisplayName("Quando POST /categories com o nome de uma categoria vazia 400 BAD_REQUEST")
    public void whenPOSTEmptyCategoryShouldReturn400BAD_REQUEST() throws Exception {
        CategoryDTO newCategory = new CategoryDTO("");
        FieldReason expectedMessage = new FieldReason("name", message("error.category.empty.name.message"));

        given(categoryService.existsByName(any())).willReturn(true);

        mockMvc.perform(post(API + "/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", equalTo("Bad Request")))
                .andExpect(jsonPath("$.detail", equalTo("Invalid request content.")))
                .andExpect(jsonPath("$.instance", equalTo(API + "/categories")))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.errors[0].field", equalTo(expectedMessage.field())))
                .andExpect(jsonPath("$.errors[0].reason", equalTo(expectedMessage.reason())))
                .andDo(print());
    }

    // ***************
    // PUT /categories/{id}
    // ***************

    @Test
    @DisplayName("Quando PUT /categories/{id} com o nome válido 200 OK")
    public void whenPUTValidCategoryShouldReturn200OK() throws Exception {
        final long id = 1L;
        Category category = new Category(id, "Test");
        CategoryDTO newCategory = new CategoryDTO("Test2");

        given(categoryService.existsById(any())).willReturn(true);
        category.setName(newCategory.getName());
        given(categoryService.update(eq(id), eq(newCategory))).willReturn(category);

        String putURI = UriComponentsBuilder.fromPath(API + "/categories").path("/{id}").build(id).toString();
        mockMvc.perform(put(putURI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id", equalTo(category.getId().intValue())))
                .andExpect(jsonPath("$.name", equalTo(newCategory.getName())))
                .andDo(print());
    }

    @Test
    @DisplayName("Quando PUT /categories/{id} com id que não existe 400 BAD_REQUEST")
    public void whenPUTInvalidCategoryIDShouldReturn400BAD_REQUEST() throws Exception {
        final long invalidId = 999L;
        String expectedDetail = message("error.resource.notfound.message", "id");

        CategoryDTO newCategory = new CategoryDTO("Test");
        given(categoryService.existsById(any())).willReturn(false);

        String putURI = UriComponentsBuilder.fromPath(API + "/categories").path("/{id}").build(invalidId).toString();
        mockMvc.perform(put(putURI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", equalTo(message("error.resource.notfound.title"))))
                .andExpect(jsonPath("$.detail", equalTo(expectedDetail)))
                .andExpect(jsonPath("$.instance", equalTo(API + "/categories/" + invalidId)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andDo(print());
    }

    @Test
    @DisplayName("Quando PUT /categories/{id} com nome que já existe 409 CONFLICT")
    public void whenPUTCategoryWithNameAlreadyExistShouldReturn409CONFLICT() throws Exception {
        final long id = 1L;
        String categoryName = "Test";
        String expectedDetail = message("error.resource.exists.message", "name");

        CategoryDTO newCategory = new CategoryDTO(categoryName);
        given(categoryService.existsByName(eq(newCategory.getName()))).willReturn(true);

        String putURI = UriComponentsBuilder.fromPath(API + "/categories").path("/{id}").build(id).toString();
        mockMvc.perform(put(putURI)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", equalTo(message("error.resource.exists.title"))))
                .andExpect(jsonPath("$.detail", equalTo(expectedDetail)))
                .andExpect(jsonPath("$.instance", equalTo(API + "/categories/" + id)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.CONFLICT.value())))
                .andDo(print());
    }

    // ***************
    // DELETE /categories/{id}
    // ***************

    @Test
    @DisplayName("Quando DELETE /categories/{id} com o ID válido 204 NO_CONTENT")
    public void whenDELETEValidCategoryIDShouldReturn204NO_CONTENT() throws Exception {
        final long id = 1L;
        given(categoryService.existsById(eq(id))).willReturn(true);

        String deleteURI = UriComponentsBuilder.fromPath(API + "/categories").path("/{id}").build(id).toString();
        mockMvc.perform(delete(deleteURI)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("Quando DELETE /categories/{id} com o ID inválido 400 BAD_REQUEST")
    public void whenDELETEInvalidCategoryIDShouldReturn400BAD_REQUEST() throws Exception {
        final long invalidId = 999L;
        String expectedDetail = message("error.resource.notfound.message", "id");
        given(categoryService.existsById(eq(invalidId))).willReturn(false);

        String deleteURI = UriComponentsBuilder.fromPath(API + "/categories").path("/{id}").build(invalidId).toString();
        mockMvc.perform(delete(deleteURI)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", equalTo(message("error.resource.notfound.title"))))
                .andExpect(jsonPath("$.detail", equalTo(expectedDetail)))
                .andExpect(jsonPath("$.instance", equalTo(API + "/categories/" + invalidId)))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andDo(print());
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