package com.rahul.smart_expense_tracker.repository;

import com.rahul.smart_expense_tracker.entity.Category;
import com.rahul.smart_expense_tracker.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Optional<Category> findByName(String name);

    // ── Get all DEFAULT (system) categories ──
    // Used when showing category dropdown to any user
    List<Category> findByCategoryTypeAndIsActiveTrue(CategoryType categoryType);

    // ── Get all CUSTOM categories created by a specific user ──
    List<Category> findByUserUserIdAndCategoryTypeAndIsActiveTrue(
            Long userId, CategoryType categoryType
    );

    // ── Get ALL categories available to a user (DEFAULT + their CUSTOM) ──
    // This is the main query — combines system + user categories
    @Query("SELECT c FROM Category c WHERE c.isActive = true " +
            "AND (c.categoryType = 'DEFAULT' OR c.user.userId = :userId)")
    List<Category> findAllCategoriesForUser(@Param("userId") Long userId);

    // ── Check if a user already has a custom category with this name ──
    // Prevents duplicate custom categories for same user
    Boolean existsByNameIgnoreCaseAndUserUserId(String name, Long userId);

    // ── Check if a default category with this name exists ──
    Boolean existsByNameIgnoreCaseAndCategoryType(String name, CategoryType categoryType);

    // ── Find category by ID, but only if it belongs to this user OR is default ──
    @Query("SELECT c FROM Category c WHERE c.categoryId = :categoryId " +
            "AND (c.categoryType = 'DEFAULT' OR c.user.userId = :userId)")
    Optional<Category> findCategoryByIdForUser(
            @Param("categoryId") Long categoryId,
            @Param("userId") Long userId
    );

}
