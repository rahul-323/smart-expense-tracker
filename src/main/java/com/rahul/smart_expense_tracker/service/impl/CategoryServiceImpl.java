package com.rahul.smart_expense_tracker.service.impl;

import com.rahul.smart_expense_tracker.dto.request.CategoryRequest;
import com.rahul.smart_expense_tracker.dto.response.CategoryResponse;
import com.rahul.smart_expense_tracker.entity.Category;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.enums.CategoryType;
import com.rahul.smart_expense_tracker.exception.BadRequestException;
import com.rahul.smart_expense_tracker.exception.DuplicateResourceException;
import com.rahul.smart_expense_tracker.exception.ResourceNotFoundException;
import com.rahul.smart_expense_tracker.mapper.CategoryMapper;
import com.rahul.smart_expense_tracker.repository.CategoryRepository;
import com.rahul.smart_expense_tracker.repository.UserRepository;
import com.rahul.smart_expense_tracker.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

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
        User user = getUserByEmail(email);

        if (categoryRepository.existsByNameIgnoreCaseAndUserUserId(request.getName().trim(), user.getUserId())) {
            throw new ResourceNotFoundException("Category", "name", request.getName().trim());
        }
        // Check 2: Does a default category with this name already exist?
        // (Don't allow user to create "Food" if system already has "Food")
        if (categoryRepository.existsByNameIgnoreCaseAndCategoryType(
                request.getName().trim(), CategoryType.DEFAULT)) {
            throw new DuplicateResourceException(
                    "A default category with name '" + request.getName() + "' already exists");
        }

        Category category = categoryMapper.toEntity(request, user);

        categoryRepository.save(category);

        return categoryMapper.toResponse(category);

    }

    @Override
    public List<CategoryResponse> getAllCategoriesForUser(String email) {
        User user = getUserByEmail(email);
        List<Category> categories = categoryRepository.findAllCategoriesForUser(user.getUserId());

        return categoryMapper.toResponseList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getDefaultCategories() {
        List<Category> defaultCategories = categoryRepository.findByCategoryTypeAndIsActiveTrue(CategoryType.DEFAULT);

        return categoryMapper.toResponseList(defaultCategories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCustomCategories(String email) {
        User user = getUserByEmail(email);
        List<Category> customCategories = categoryRepository
                .findByUserUserIdAndCategoryTypeAndIsActiveTrue(user.getUserId(), CategoryType.CUSTOM);
        return categoryMapper.toResponseList(customCategories);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Category category = categoryRepository
                .findCategoryByIdForUser(categoryId, user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Category"));

        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request, String email)
            throws BadRequestException {
        User user = getUserByEmail(email);

        Category category = categoryRepository
                .findCategoryByIdForUser(categoryId, user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        if (category.getCategoryType() == CategoryType.DEFAULT) {
            throw new BadRequestException("Cannot modify default Categories");
        }
        if (category.getUser() == null || !category.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("You can only update your own categories");
        }
        if (!category.getName().equalsIgnoreCase(request.getName().trim()) &&
                categoryRepository.existsByNameIgnoreCaseAndUserUserId(
                        request.getName().trim(), user.getUserId())) {
            throw new DuplicateResourceException(
                    "Category", "name", request.getName());
        }

        categoryMapper.updateEntity(category, request);
        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);

    }

    @Override
    public void deleteCategory(Long categoryId, String email) throws BadRequestException {
        User user = getUserByEmail(email);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        if (category.getCategoryType() == CategoryType.DEFAULT) {
            throw new BadRequestException("Cannot delete default categories");
        }

        if (category.getUser() == null || !category.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("You can only delete your own categories");
        }

        category.setIsActive(false);
        categoryRepository.save(category);
    }

}
