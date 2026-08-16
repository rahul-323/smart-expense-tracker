package com.rahul.smart_expense_tracker.controller;

import com.rahul.smart_expense_tracker.dto.request.RecurringExpenseRequest;
import com.rahul.smart_expense_tracker.dto.response.ApiResponse;
import com.rahul.smart_expense_tracker.dto.response.RecurringExpenseResponse;
import com.rahul.smart_expense_tracker.service.RecurringExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-expenses")
public class RecurringExpenseController {

    @Autowired
    private RecurringExpenseService recurringExpenseService;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecurringExpenseResponse>> createRecurring(
            @Valid @RequestBody RecurringExpenseRequest request) {
        RecurringExpenseResponse resp = recurringExpenseService.createRecurring(request, getCurrentUserEmail());
        return new ResponseEntity<>(ApiResponse.success("Recurring expense created", resp), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecurringExpenseResponse>>> getAll() {
        List<RecurringExpenseResponse> list = recurringExpenseService.getAllForUser(getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Recurring expenses fetched", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecurringExpenseResponse>> getById(@PathVariable("id") Long id) {
        RecurringExpenseResponse resp = recurringExpenseService.getById(id, getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Recurring expense fetched", resp));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RecurringExpenseResponse>> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody RecurringExpenseRequest request) {
        RecurringExpenseResponse resp = recurringExpenseService.updateRecurring(id, request, getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Recurring expense updated", resp));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable("id") Long id,
            @RequestParam(required = false) Boolean active) {
        recurringExpenseService.toggleActive(id, getCurrentUserEmail(), active);
        return ResponseEntity.ok(ApiResponse.success("Recurring expense updated", null));
    }

    // Compatibility endpoint: /api/recurring-expenses/{id}/toggle
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleEndpoint(@PathVariable("id") Long id,
            @RequestParam(required = false) Boolean active) {
        recurringExpenseService.toggleActive(id, getCurrentUserEmail(), active);
        return ResponseEntity.ok(ApiResponse.success("Recurring expense toggled", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        recurringExpenseService.deleteRecurring(id, getCurrentUserEmail());
        return ResponseEntity.ok(ApiResponse.success("Recurring expense deleted", null));
    }
}
