package com.rahul.smart_expense_tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budgets", indexes = {
        @Index(name = "idx_budget_user", columnList = "user_user_id"),
        @Index(name = "idx_budget_month_year", columnList = "budget_month,budget_year")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetId;

    @NotNull(message = "Budget limit is required")
    @DecimalMin(value = "0.01", message = "Budget limit must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal budgetLimit;

    @Column(name = "budget_month",nullable = false)
    private Integer month;

    @Column(name = "budget_year",nullable = false)
    private Integer year;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // Percentage of budgetLimit at which a WARNING status kicks in (see BudgetServiceImpl).
    @Column(nullable = false)
    @Builder.Default
    private Integer alertThreshold = 80;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_category_id", nullable = false)
    private Category category;
}