package com.rahul.smart_expense_tracker.controller;

import com.rahul.smart_expense_tracker.dto.request.ExpenseRequest;
import com.rahul.smart_expense_tracker.dto.response.ApiResponse;
import com.rahul.smart_expense_tracker.dto.response.ExpenseResponse;
import com.rahul.smart_expense_tracker.dto.response.PagedResponse;
import com.rahul.smart_expense_tracker.enums.ExpenseStatus;
import com.rahul.smart_expense_tracker.enums.PaymentMethod;
import com.rahul.smart_expense_tracker.service.ExpenseService;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // POST /api/expenses
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @Valid @RequestBody ExpenseRequest request
    ) {
        ExpenseResponse response = expenseService.createExpense(request, getCurrentUserEmail());
        return new ResponseEntity<>(
                ApiResponse.success("Expense created successfully", response),
                HttpStatus.CREATED
        );
    }

    // GET /api/expenses?page=0&size=10&sortBy=expenseDate&sortDir=desc
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseResponse>>> getAllExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "expenseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        PagedResponse<ExpenseResponse> response = expenseService
                .getAllExpenses(getCurrentUserEmail(), page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Expenses fetched successfully", response));
    }

    // GET /api/expenses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(
            @PathVariable("id") Long expenseId
    ) {
        ExpenseResponse response = expenseService.getExpenseById(expenseId, getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Expense fetched successfully", response));
    }

    // PUT /api/expenses/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable("id") Long expenseId,
            @Valid @RequestBody ExpenseRequest request
    ) {
        ExpenseResponse response = expenseService.updateExpense(expenseId, request, getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", response));
    }

    // DELETE /api/expenses/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable("id") Long expenseId
    ) {
        expenseService.deleteExpense(expenseId, getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
    }

    // GET /api/expenses/filter?categoryId=1&startDate=...&endDate=...
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseResponse>>> filterExpenses(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) ExpenseStatus status,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "expenseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        PagedResponse<ExpenseResponse> response = expenseService.filterExpenses(
                getCurrentUserEmail(), categoryId, startDate, endDate,
                minAmount, maxAmount, paymentMethod, status, tags,
                page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.success("Expenses filtered successfully", response));
    }

    // GET /api/expenses/search?keyword=lunch
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseResponse>>> searchExpenses(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponse<ExpenseResponse> response = expenseService
                .searchExpenses(getCurrentUserEmail(), keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("Search completed", response));
    }


}
