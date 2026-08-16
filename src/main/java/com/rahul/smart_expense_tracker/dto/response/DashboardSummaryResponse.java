package com.rahul.smart_expense_tracker.dto.response;

import lombok.*;
import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardSummaryResponse {

    private String monthName;
    private Integer month;
    private Integer year;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;

    private Double savingPercentage;

    private BigDecimal totalBudgetLimit;  // sum of all budgets this month
    private BigDecimal totalBudgetSpent;  // = totalExpense
    private Double budgetUtilization;     // (spent/limit) * 100

    private Long expenseCount;
    private String topCategory;
    private BigDecimal topCategoryAmount;

}
