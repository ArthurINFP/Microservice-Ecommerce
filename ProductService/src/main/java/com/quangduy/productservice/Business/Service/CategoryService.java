package com.quangduy.productservice.Business.Service;

import com.quangduy.productservice.Business.Domain.Category;
import com.quangduy.productservice.Persistence.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Retrieve all categories
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Retrieve a category by ID
    @Transactional(readOnly = true)
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
    }

    // Create a new category
    @Transactional
    public Category createCategory(Category category) {
        if (category.getCategoryId() != null) {
            throw new IllegalArgumentException("New category cannot have an existing ID");
        }
        categoryRepository.findByCategoryName(category.getCategoryName())
                .ifPresent(a -> {
                    throw new IllegalArgumentException("Category already exists: " + category.getCategoryName());
                });
        return categoryRepository.save(category);
    }

    // Update an existing category
    @Transactional
    public Category updateCategory(Integer id, Category categoryDetails) {
        Category existingCategory = getCategoryById(id);
        categoryRepository.findByCategoryName(categoryDetails.getCategoryName())
                .ifPresent(a -> {
                    if (!a.getCategoryId().equals(id)) {
                        throw new IllegalArgumentException("Category already exists: " + categoryDetails.getCategoryName());
                    }
                });
        existingCategory.setCategoryName(categoryDetails.getCategoryName());
        existingCategory.setImageUrl(categoryDetails.getImageUrl());


        return categoryRepository.save(existingCategory);
    }


    // Delete a category
    @Transactional
    public void deleteCategory(Integer id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
