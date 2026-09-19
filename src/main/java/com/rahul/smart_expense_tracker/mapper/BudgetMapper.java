package com.rahul.smart_expense_tracker.mapper;

import com.rahul.smart_expense_tracker.dto.response.BudgetResponse;
import com.rahul.smart_expense_tracker.entity.Budget;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

    public BudgetResponse toResponse(Budget budget) {
        if (budget == null) {
            return null;
        }

        return BudgetResponse.builder()
                .budgetId(budget.getBudgetId())
                .categoryId(budget.getCategory() != null ? budget.getCategory().getCategoryId() : null)
                .categoryName(budget.getCategory() != null ? budget.getCategory().getName() : null)
                .categoryIcon(budget.getCategory() != null ? budget.getCategory().getIcon() : null)
                .categoryColor(budget.getCategory() != null ? budget.getCategory().getColor() : null)
                .budgetLimit(budget.getBudgetLimit())
                .month(budget.getMonth())
                .year(budget.getYear())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .alertThreshold(budget.getAlertThreshold())
                .build();
    }
}