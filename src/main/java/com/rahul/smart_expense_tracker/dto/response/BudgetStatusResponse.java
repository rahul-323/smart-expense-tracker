package com.rahul.smart_expense_tracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetStatusResponse {

    private Long budgetId;
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private String categoryColor;
    private BigDecimal budgetLimit;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private Double percentUsed;
    private String status; // ON_TRACK | WARNING | EXCEEDED
    private Integer month;
    private Integer year;
}