package com.rahul.smart_expense_tracker.service;

import com.rahul.smart_expense_tracker.dto.request.BudgetRequest;
import com.rahul.smart_expense_tracker.dto.response.BudgetResponse;
import com.rahul.smart_expense_tracker.dto.response.BudgetStatusResponse;

import java.util.List;

public interface BudgetService {

    BudgetResponse createBudget(String email, BudgetRequest request);

    /**
     * Budgets for the current calendar month — matches "Get All Budgets (Current Month)".
     */
    List<BudgetResponse> getAllBudgets(String email);

    BudgetResponse getBudgetById(String email, Long budgetId);

    /**
     * Budgets for a specific month/year — matches GET /budgets/month/{year}/{month}.
     */
    List<BudgetResponse> getBudgetsForMonth(String email, Integer year, Integer month);

    BudgetResponse updateBudget(String email, Long budgetId, BudgetRequest request);

    void deleteBudget(String email, Long budgetId);

    /**
     * Spent vs. limit status for the current month, per category.
     */
    List<BudgetStatusResponse> getBudgetStatus(String email);
}