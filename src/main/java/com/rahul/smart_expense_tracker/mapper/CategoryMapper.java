package com.rahul.smart_expense_tracker.mapper;
import com.rahul.smart_expense_tracker.dto.request.CategoryRequest;
import com.rahul.smart_expense_tracker.dto.response.CategoryResponse;
import com.rahul.smart_expense_tracker.entity.Category;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.enums.CategoryType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    // ── Convert Request DTO → Entity (for creating new category) ──
    public Category toEntity(CategoryRequest request, User user) {
        return Category.builder()
                .name(request.getName().trim())
                .icon(request.getIcon())
                .color(request.getColor())
                .categoryType(CategoryType.CUSTOM)    // User-created = always CUSTOM
                .isActive(true)
                .user(user)                            // Link to the user who created it
                .build();
    }

    // ── Convert Entity → Response DTO ──
    public CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .icon(category.getIcon())
                .color(category.getColor())
                .categoryType(category.getCategoryType().name())
                .isActive(category.getIsActive())
                .isEditable(category.getCategoryType() == CategoryType.CUSTOM)
                .build();
    }

    // ── Convert List of Entities → List of Response DTOs ──
    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream()
                .map(this::toResponse)        // Apply toResponse to each category
                .collect(Collectors.toList());
    }

    // ── Update existing entity from request (for PUT) ──
    public void updateEntity(Category category, CategoryRequest request) {
        category.setName(request.getName().trim());

        // Only update icon/color if provided (don't overwrite with null)
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        if (request.getColor() != null) {
            category.setColor(request.getColor());
        }
    }


}
