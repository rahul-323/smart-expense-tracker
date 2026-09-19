package com.rahul.smart_expense_tracker.service.impl;


import com.rahul.smart_expense_tracker.dto.request.CategoryRequest;
import com.rahul.smart_expense_tracker.dto.response.CategoryResponse;
import com.rahul.smart_expense_tracker.entity.Category;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.enums.CategoryType;
import com.rahul.smart_expense_tracker.enums.UserRole;
import com.rahul.smart_expense_tracker.mapper.CategoryMapper;
import com.rahul.smart_expense_tracker.repository.CategoryRepository;
import com.rahul.smart_expense_tracker.repository.UserRepository;
import com.rahul.smart_expense_tracker.exception.BadRequestException;
import com.rahul.smart_expense_tracker.exception.DuplicateResourceException;
import com.rahul.smart_expense_tracker.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category Service Unit Tests")
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User testUser;
    private Category customCategory;
    private Category defaultCategory;
    private CategoryRequest categoryRequest;


    private final String EMAIL="rahultest@gmail.com";


    @BeforeEach
    void SetUp(){
        testUser=User.builder()
                .userId(1L)
                .name("Rahul")
                .email(EMAIL)
                .userRole(UserRole.ROLE_USER)
                .build();

        customCategory = Category.builder()
                .categoryId(15L)
                .name("Side Hustle")
                .icon("💼")
                .categoryType(CategoryType.CUSTOM)
                .isActive(true)
                .user(testUser)
                .build();

        defaultCategory = Category.builder()
                .categoryId(1L)
                .name("Food & Dining")
                .icon("🍔")
                .categoryType(CategoryType.DEFAULT)
                .isActive(true)
                .user(null)
                .build();

        categoryRequest = CategoryRequest.builder()
                .name("Side Hustle")
                .icon("💼")
                .color("#9B59B6")
                .build();

    }

    // ═══════════════════════════════════════════
    // CREATE CATEGORY — HAPPY PATH
    // ═══════════════════════════════════════════

    @Test
    @DisplayName("Should create custom category successfully")
    void createCategory_Success() {
// ── ARRANGE (given) ──
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(categoryRepository.existsByNameIgnoreCaseAndUserUserId(anyString(), anyLong()))
                .thenReturn(false);
        when(categoryRepository.existsByNameIgnoreCaseAndCategoryType(anyString(), eq(CategoryType.DEFAULT)))
                .thenReturn(false);
        when(categoryMapper.toEntity(any(), any())).thenReturn(customCategory);
        when(categoryRepository.save(any(Category.class))).thenReturn(customCategory);
        when(categoryMapper.toResponse(any())).thenReturn(
                CategoryResponse.builder()
                        .categoryId(15L)
                        .name("Side Hustle")
                        .categoryType("CUSTOM")
                        .isEditable(true)
                        .build()
        );

// ── ACT (when) ──
        CategoryResponse result = categoryService.createCategory(categoryRequest, EMAIL);

// ── ASSERT (then) ──
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Side Hustle");
        assertThat(result.getCategoryType()).isEqualTo("CUSTOM");
        assertThat(result.getIsEditable()).isTrue();

// Verify save was actually called once
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    // ═══════════════════════════════════════════
// CREATE CATEGORY — DUPLICATE NAME
// ═══════════════════════════════════════════
    @Test
    @DisplayName("Should throw DuplicateResourceException when custom category name exists")
    void createCategory_DuplicateName_ThrowsException() {
// ARRANGE
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(categoryRepository.existsByNameIgnoreCaseAndUserUserId(anyString(), anyLong()))
                .thenReturn(true); // simulate duplicate exists

// ACT + ASSERT
        assertThatThrownBy(() -> categoryService.createCategory(categoryRequest, EMAIL))
                .isInstanceOf(DuplicateResourceException.class);

// Verify save was NEVER called (because it failed before saving)
        verify(categoryRepository, never()).save(any());
    }

    // ═══════════════════════════════════════════
// CREATE CATEGORY — USER NOT FOUND
// ═══════════════════════════════════════════
    @Test
    @DisplayName("Should throw ResourceNotFoundException when user does not exist")
    void createCategory_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.createCategory(categoryRequest, EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");

        verify(categoryRepository, never()).save(any());
    }

    // ═══════════════════════════════════════════
// GET CATEGORY BY ID — SUCCESS
// ═══════════════════════════════════════════
    @Test
    @DisplayName("Should fetch category by ID successfully")
    void getCategoryById_Success() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findCategoryByIdForUser(15L, 1L))
                .thenReturn(Optional.of(customCategory));
        when(categoryMapper.toResponse(customCategory)).thenReturn(
                CategoryResponse.builder().categoryId(15L).name("Side Hustle").build()
        );

        CategoryResponse result = categoryService.getCategoryById(15L, EMAIL);

        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isEqualTo(15L);
    }

    // ═══════════════════════════════════════════
// GET CATEGORY BY ID — NOT FOUND
// ═══════════════════════════════════════════
    @Test
    @DisplayName("Should throw exception when category not found")
    void getCategoryById_NotFound_ThrowsException() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findCategoryByIdForUser(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(999L, EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category");
    }

    // ═══════════════════════════════════════════
// UPDATE — CANNOT MODIFY DEFAULT CATEGORY
// ═══════════════════════════════════════════
    @Test
    @DisplayName("Should throw BadRequestException when updating default category")
    void updateCategory_DefaultCategory_ThrowsException() {
        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(testUser));
        when(categoryRepository.findCategoryByIdForUser(1L, testUser.getUserId()))
                .thenReturn(Optional.of(defaultCategory));

        assertThatThrownBy(() -> categoryService.updateCategory(1L, categoryRequest, EMAIL))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot modify default categories");
        verify(categoryRepository, never()).save(any());
    }

    // ═══════════════════════════════════════════
// DELETE — SOFT DELETE (isActive = false)
// ═══════════════════════════════════════════
    @Test
    @DisplayName("Should soft delete custom category")
    void deleteCategory_Success() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(15L)).thenReturn(Optional.of(customCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(customCategory);

        categoryService.deleteCategory(15L, EMAIL);

// Verify it was soft-deleted (isActive set to false), not hard-deleted
        assertThat(customCategory.getIsActive()).isFalse();
        verify(categoryRepository, times(1)).save(customCategory);
        verify(categoryRepository, never()).delete(any()); // never hard delete
    }

    // ═══════════════════════════════════════════
// DELETE — CANNOT DELETE DEFAULT CATEGORY
// ═══════════════════════════════════════════
    @Test
    @DisplayName("Should throw exception when deleting default category")
    void deleteCategory_DefaultCategory_ThrowsException() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(defaultCategory));

        assertThatThrownBy(() -> categoryService.deleteCategory(1L, EMAIL))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot delete default categories");
    }

}
