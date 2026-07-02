package com.rahul.smart_expense_tracker.dto.response;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private Long categoryId;
    private String name;
    private String icon;
    private String color;
    private String categoryType;      // "DEFAULT" or "CUSTOM"
    private Boolean isActive;
    private Boolean isEditable;

}
