package com.rahul.smart_expense_tracker.controller;

import com.rahul.smart_expense_tracker.dto.request.CategoryRequest;
import com.rahul.smart_expense_tracker.dto.response.ApiResponse;
import com.rahul.smart_expense_tracker.dto.response.CategoryResponse;
import com.rahul.smart_expense_tracker.service.CategoryService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    private String getCurrentUserEmail() {
        return Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest) {
        String email = getCurrentUserEmail();

        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest, email);

        return new ResponseEntity<>(
                ApiResponse.success("Category created successfully", categoryResponse),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        String email = getCurrentUserEmail();

        List<CategoryResponse> categories = categoryService.getAllCategoriesForUser(email);

        return new ResponseEntity<>(
                ApiResponse.success("Categories Fetched", categories), HttpStatus.OK);
    }

    @GetMapping("/default")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getDeafultCategories() {

        List<CategoryResponse> defaultCategories = categoryService.getDefaultCategories();
        return new ResponseEntity<>(
                ApiResponse.success("Default categories fetched successfully", defaultCategories), HttpStatus.OK);
    }

    @GetMapping("/custom")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCustomCategories() {
        String email = getCurrentUserEmail();

        List<CategoryResponse> customCategories = categoryService.getCustomCategories(email);
        return new ResponseEntity<>(
                ApiResponse.success("Custom categories fetched", customCategories), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable("id") Long categoryId) {
        String email = getCurrentUserEmail();
        CategoryResponse categoryResponse = categoryService.getCategoryById(categoryId, email);

        return ResponseEntity.ok(ApiResponse.success("Category fetched successfully", categoryResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable("id") Long categoryId,
            @Valid @RequestBody CategoryRequest categoryRequest) throws BadRequestException {
        String email = getCurrentUserEmail();

        CategoryResponse categoryResponse = categoryService.updateCategory(categoryId, categoryRequest, email);

        return new ResponseEntity<>(
                ApiResponse.success("Category updated successfully", categoryResponse), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable("id") Long categoryId) throws BadRequestException {
        String email = getCurrentUserEmail();
        categoryService.deleteCategory(categoryId, email);

        return ResponseEntity.ok(
                ApiResponse.success("Category deleted successfully", null));
    }

}
