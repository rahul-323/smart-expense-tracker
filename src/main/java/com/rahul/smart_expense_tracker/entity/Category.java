package com.rahul.smart_expense_tracker.entity;

//(depends on User)

import com.rahul.smart_expense_tracker.enums.CategoryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "category",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"names","user_user_id"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Category name must be under 50 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 10, message = "Icon must be under 10 characters")
    private String icon;

    @Size(max = 10,message = "Color must be a valid hex code")
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType categoryType;

    @Column(nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_user_id")
    private User user;
}
