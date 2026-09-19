package com.rahul.smart_expense_tracker.controller;

import com.rahul.smart_expense_tracker.dto.request.BudgetRequest;
import com.rahul.smart_expense_tracker.dto.response.ApiResponse;
import com.rahul.smart_expense_tracker.dto.response.BudgetResponse;
import com.rahul.smart_expense_tracker.dto.response.BudgetStatusResponse;
import com.rahul.smart_expense_tracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    // POST /api/budgets -> "Set Budget — ..."
    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(
            Authentication authentication,
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.createBudget(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Budget created successfully", response));
    }

    // GET /api/budgets -> "Get All Budgets (Current Month)"
    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getAllBudgets(Authentication authentication) {
        List<BudgetResponse> response = budgetService.getAllBudgets(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Budgets fetched successfully", response));
    }

    // GET /api/budgets/month/{year}/{month} -> "Get Budgets for Specific Month"
    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgetsForMonth(
            Authentication authentication,
            @PathVariable Integer year,
            @PathVariable Integer month) {
        List<BudgetResponse> response = budgetService.getBudgetsForMonth(authentication.getName(), year, month);
        return ResponseEntity.ok(ApiResponse.success("Budgets fetched successfully", response));
    }

    // GET /api/budgets/status -> "⭐ Get Budget Status (Spent vs Limit)"
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<BudgetStatusResponse>>> getBudgetStatus(Authentication authentication) {
        List<BudgetStatusResponse> response = budgetService.getBudgetStatus(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Budget status fetched successfully", response));
    }

    // GET /api/budgets/{budgetId} -> "Get Budget by ID"
    @GetMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudgetById(
            Authentication authentication,
            @PathVariable Long budgetId) {
        BudgetResponse response = budgetService.getBudgetById(authentication.getName(), budgetId);
        return ResponseEntity.ok(ApiResponse.success("Budget fetched successfully", response));
    }

    // PUT /api/budgets/{budgetId} -> "Update Budget"
    @PutMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            Authentication authentication,
            @PathVariable Long budgetId,
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.updateBudget(authentication.getName(), budgetId, request);
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", response));
    }

    // DELETE /api/budgets/{budgetId} -> "Delete Budget"
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            Authentication authentication,
            @PathVariable Long budgetId) {
        budgetService.deleteBudget(authentication.getName(), budgetId);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully", null));
    }
}