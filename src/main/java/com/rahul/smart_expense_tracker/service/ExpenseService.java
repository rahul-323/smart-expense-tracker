package com.rahul.smart_expense_tracker.service;

import com.rahul.smart_expense_tracker.dto.request.ExpenseRequest;
import com.rahul.smart_expense_tracker.dto.response.ExpenseResponse;
import com.rahul.smart_expense_tracker.dto.response.PagedResponse;
import com.rahul.smart_expense_tracker.enums.ExpenseStatus;
import com.rahul.smart_expense_tracker.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseResponse createExpense(ExpenseRequest request, String email);

    PagedResponse<ExpenseResponse> getAllExpenses(
            String email, int page, int size, String sortBy, String sortDir
    );

    ExpenseResponse getExpenseById(Long expenseId, String email);

    ExpenseResponse updateExpense(Long expenseId, ExpenseRequest request, String email);

    void deleteExpense(Long expenseId, String email);

    PagedResponse<ExpenseResponse> filterExpenses(
            String email,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            PaymentMethod paymentMethod,
            ExpenseStatus status,
            List<String> tags,
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    PagedResponse<ExpenseResponse> searchExpenses(
            String email, String keyword, int page, int size
    );

}
