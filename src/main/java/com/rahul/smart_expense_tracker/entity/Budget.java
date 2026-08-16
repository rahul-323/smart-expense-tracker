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
        @Index(name = "idx_budget_month_year", columnList = "month, year")
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

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_category_id", nullable = false)
    private Category category;
}
