package com.rahul.smart_expense_tracker.service;

import com.rahul.smart_expense_tracker.dto.request.CategoryRequest;
import com.rahul.smart_expense_tracker.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    // Create a new custom category for the logged-in user
    CategoryResponse createCategory(CategoryRequest request, String email);

    // Get ALL categories available to the user (default + custom)
    List<CategoryResponse> getAllCategoriesForUser(String email);

    // Get only DEFAULT (system) categories
    List<CategoryResponse> getDefaultCategories();

    // Get only CUSTOM categories created by the user
    List<CategoryResponse> getCustomCategories(String email);

    // Get a single category by ID
    CategoryResponse getCategoryById(Long categoryId, String email);

    // Update a custom category (can't update default ones)
    CategoryResponse updateCategory(Long categoryId, CategoryRequest request, String email);

    // Soft delete a custom category (can't delete default ones)
    void deleteCategory(Long categoryId, String email);
}
