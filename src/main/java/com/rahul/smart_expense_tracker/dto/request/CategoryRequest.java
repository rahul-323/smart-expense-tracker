package com.rahul.smart_expense_tracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be 2-50 characters")
    private String name;

    @Size(max = 10, message = "Icon must be under 10 characters")
    private String icon;

    @Size(max = 7, message = "Color must be a valid hex code")
    private String color;

}
