package com.prpa.trivia.service;

import com.prpa.trivia.model.Category;
import com.prpa.trivia.model.dto.CategoryDTO;
import com.prpa.trivia.model.exceptions.SpecificResourceNotFoundException;
import com.prpa.trivia.repository.CategoryRepository;
import com.prpa.trivia.resources.OffsetPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll(int offset, int limit) {
        if (offset < 0) throw new IllegalArgumentException("Offset must be greater than 1.");
        if (limit < 1) throw new IllegalArgumentException("Limit must be greater than 1.");

        Pageable page = OffsetPageRequest.of(offset, limit);
        return categoryRepository.findAll(page).toList();
    }

    public Category save(CategoryDTO category) {
        return categoryRepository.save(new Category(null, category.getName()));
    }

    public boolean existsByName(String categoryName) {
        return categoryRepository.existsByName(categoryName);
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category update(Long id, CategoryDTO newCategory) {
        Category found = findById(id).orElseThrow(() -> new SpecificResourceNotFoundException("id"));
        if (found.getName().equals(newCategory.getName())) return found;
        found.setName(newCategory.getName());
        return categoryRepository.save(found);
    }

    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> findOrSaveAll(List<CategoryDTO> category) {
        if (category == null) return List.of();
        List<Category> categories = new ArrayList<>();
        for (CategoryDTO dto : category) {
            dto.setName(dto.getName() != null ? dto.getName().trim() : "");
            categories.add(categoryRepository.findByName(dto.getName()).orElseGet(() -> save(dto)));
        }
        return categories;
    }
}
