package com.rahul.smart_expense_tracker.service;

public interface RecurringExpenseService {
    /**
     * Process recurring expenses that are due (create expense records, update next
     * run, etc.).
     * Implementation is intentionally minimal here as a scaffold.
     */
    void processDueRecurringExpenses();

    com.rahul.smart_expense_tracker.dto.response.RecurringExpenseResponse createRecurring(
            com.rahul.smart_expense_tracker.dto.request.RecurringExpenseRequest request, String email);

    java.util.List<com.rahul.smart_expense_tracker.dto.response.RecurringExpenseResponse> getAllForUser(String email);

    com.rahul.smart_expense_tracker.dto.response.RecurringExpenseResponse getById(Long id, String email);

    com.rahul.smart_expense_tracker.dto.response.RecurringExpenseResponse updateRecurring(Long id,
            com.rahul.smart_expense_tracker.dto.request.RecurringExpenseRequest request, String email);

    void toggleActive(Long id, String email, Boolean active);

    void deleteRecurring(Long id, String email);
}
