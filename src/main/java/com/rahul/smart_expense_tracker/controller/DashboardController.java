package com.rahul.smart_expense_tracker.controller;

import com.rahul.smart_expense_tracker.dto.response.*;
import com.rahul.smart_expense_tracker.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    // GET /api/dashboard/summary
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {
        DashboardSummaryResponse response = dashboardService.getSummary(getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary fetched", response));
    }

    // GET /api/dashboard/summary/{year}/{month}
    @GetMapping("/summary/{year}/{month}")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummaryForMonth(
            @PathVariable Integer year,
            @PathVariable Integer month
    ) {
        DashboardSummaryResponse response = dashboardService
                .getSummaryForMonth(year, month, getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary for " + month + "/" + year, response));
    }

    // GET /api/dashboard/category-breakdown
    @GetMapping("/category-breakdown")
    public ResponseEntity<ApiResponse<List<CategoryBreakdownResponse>>> getCategoryBreakdown() {
        List<CategoryBreakdownResponse> response = dashboardService
                .getCategoryBreakdown(getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Category breakdown fetched", response));
    }

    // GET /api/dashboard/category-breakdown/{year}/{month}
    @GetMapping("/category-breakdown/{year}/{month}")
    public ResponseEntity<ApiResponse<List<CategoryBreakdownResponse>>> getCategoryBreakdownForMonth(
            @PathVariable Integer year,
            @PathVariable Integer month
    ) {
        List<CategoryBreakdownResponse> response = dashboardService
                .getCategoryBreakdownForMonth(year, month, getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Category breakdown for " + month + "/" + year, response));
    }

    // GET /api/dashboard/monthly-trend
    @GetMapping("/monthly-trend")
    public ResponseEntity<ApiResponse<List<MonthlyTrendResponse>>> getMonthlyTrend() {
        List<MonthlyTrendResponse> response = dashboardService.getMonthlyTrend(getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Monthly trend fetched", response));
    }

    // GET /api/dashboard/daily-expenses/{year}/{month}
    @GetMapping("/daily-expenses/{year}/{month}")
    public ResponseEntity<ApiResponse<List<DailyExpenseResponse>>> getDailyExpenses(
            @PathVariable Integer year,
            @PathVariable Integer month
    ) {
        List<DailyExpenseResponse> response = dashboardService
                .getDailyExpenses(year, month, getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Daily expenses fetched", response));
    }

    // GET /api/dashboard/top-expenses
    @GetMapping("/top-expenses")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getTopExpenses() {
        List<ExpenseResponse> response = dashboardService.getTopExpenses(getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Top expenses fetched", response));
    }

}
