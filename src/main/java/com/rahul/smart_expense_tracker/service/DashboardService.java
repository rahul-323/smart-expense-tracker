package com.rahul.smart_expense_tracker.service;

import com.rahul.smart_expense_tracker.dto.response.*;

import java.util.List;

public interface DashboardService {

    DashboardSummaryResponse getSummary(String email);

    DashboardSummaryResponse getSummaryForMonth(Integer year, Integer month, String email);

    List<CategoryBreakdownResponse> getCategoryBreakdown(String email);

    List<CategoryBreakdownResponse> getCategoryBreakdownForMonth(Integer year, Integer month, String email);

    List<MonthlyTrendResponse> getMonthlyTrend(String email);

    List<DailyExpenseResponse> getDailyExpenses(Integer year, Integer month, String email);

    List<ExpenseResponse> getTopExpenses(String email);

}
