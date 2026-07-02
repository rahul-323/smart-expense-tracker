package com.rahul.smart_expense_tracker.service.impl;

import com.rahul.smart_expense_tracker.dto.request.CategoryRequest;
import com.rahul.smart_expense_tracker.dto.response.CategoryResponse;
import com.rahul.smart_expense_tracker.entity.Category;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.enums.CategoryType;
import com.rahul.smart_expense_tracker.exception.DuplicateResourceException;
import com.rahul.smart_expense_tracker.exception.ResourceNotFoundException;
import com.rahul.smart_expense_tracker.mapper.CategoryMapper;
import com.rahul.smart_expense_tracker.repository.CategoryRepository;
import com.rahul.smart_expense_tracker.repository.UserRepository;
import com.rahul.smart_expense_tracker.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    // ─── Helper: Get User from email ───
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }


    @Override
    public CategoryResponse createCategory(CategoryRequest request, String email) {
        User user=getUserByEmail(email);

        if(categoryRepository.
                existsByNameIgnoreCaseAndUserUserId(request.getName().trim(),user.getUserId())){
            throw new ResourceNotFoundException("Category", "name", request.getName().trim());
        }
        // Check 2: Does a default category with this name already exist?
        // (Don't allow user to create "Food" if system already has "Food")
        if (categoryRepository.existsByNameIgnoreCaseAndCategoryType(
                request.getName().trim(), CategoryType.DEFAULT)) {
            throw new DuplicateResourceException(
                    "A default category with name '" + request.getName() + "' already exists"
            );
        }

        Category category=categoryMapper.toEntity(request,user);

        categoryRepository.save(category);

        return  categoryMapper.toResponse(category);

    }

    @Override
    public List<CategoryResponse> getAllCategoriesForUser(String email) {
        return List.of();
    }

    @Override
    public List<CategoryResponse> getDefaultCategories() {
        return List.of();
    }

    @Override
    public List<CategoryResponse> getCustomCategories(String email) {
        return List.of();
    }

    @Override
    public CategoryResponse getCategoryById(Long categoryId, String email) {
        return null;
    }

    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request, String email) {
        return null;
    }

    @Override
    public void deleteCategory(Long categoryId, String email) {

    }


}
