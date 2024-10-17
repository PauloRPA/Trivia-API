package com.prpa.trivia.resources;

import com.prpa.trivia.model.Category;
import com.prpa.trivia.model.dto.CategoryDTO;
import com.prpa.trivia.model.exceptions.ResourceAlreadyExistException;
import com.prpa.trivia.model.exceptions.SpecificResourceNotFoundException;
import com.prpa.trivia.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    public static final String API = "/api/v1";
    public static final String CATEGORIES = API + "/categories";

    public static final Integer DEFAULT_OFFSET = 0;
    public static final Integer DEFAULT_LIMIT = 10;

    public static final Integer MAX_LIMIT = 100;

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Busca categoria por ID (Long).", method = "GET")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria com id especificado encontrada."),
            @ApiResponse(responseCode = "400", description = "Categoria com id especificado não encontrada.")
    })
    @GetMapping(value = "/categories/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> getCategories(@PathVariable("id") long id) {
        Category found = categoryService.findById(id).orElseThrow(() ->
                new SpecificResourceNotFoundException("id", String.valueOf(id)));

        return ResponseEntity.ok(found);
    }

    @Operation(summary = "Busca categorias dentro do offset e limit.", method = "GET")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categorias retornadas."),
    })
    @GetMapping(value = "/categories", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Category>> getCategories(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        offset = offset < 0 ? DEFAULT_OFFSET : offset;
        limit = limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);

        List<Category> found = categoryService.findAll(offset, limit);

        return ResponseEntity.ok(found);
    }

    @Operation(summary = "Insere uma nova categoria.", method = "POST")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria criada."),
            @ApiResponse(responseCode = "409", description = "Categoria com nome especificado já existe."),
            @ApiResponse(responseCode = "400", description = "Categoria inválida (nome vazio).")
    })
    @PostMapping(value = "/categories", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> postCategories(@Valid @RequestBody CategoryDTO newCategory) {
        if (categoryService.existsByName(newCategory.getName())) {
            throw new ResourceAlreadyExistException("name", newCategory.getName());
        }

        Category created = categoryService.save(newCategory);
        URI locationURI = UriComponentsBuilder.fromPath(CATEGORIES)
                .path("{id}")
                .build(created.getId());
        return ResponseEntity.created(locationURI).body(created);
    }

    @Operation(summary = "Altera uma categoria por ID (long).", method = "PUT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria alterada com sucesso."),
            @ApiResponse(responseCode = "409", description = "Categoria com nome especificado já existe."),
            @ApiResponse(responseCode = "400", description = "Categoria inválida ou ID não encontrado.")
    })
    @PutMapping(value = "/categories/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> postCategories(
            @PathVariable("id") Long id,
            @Valid @RequestBody CategoryDTO newCategory) {

        if (categoryService.existsByName(newCategory.getName()))
            throw new ResourceAlreadyExistException("name", newCategory.getName());

        if (!categoryService.existsById(id))
            throw new SpecificResourceNotFoundException("id", String.valueOf(id));

        Category updated = categoryService.update(id, newCategory);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Remove uma categoria por ID (long).", method = "DELETE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria removida com sucesso."),
            @ApiResponse(responseCode = "400", description = "Categoria com id especificado não encontrada.")
    })
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> postCategories(@PathVariable("id") Long id) {

        if (!categoryService.existsById(id))
            throw new SpecificResourceNotFoundException("id", String.valueOf(id));

        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}