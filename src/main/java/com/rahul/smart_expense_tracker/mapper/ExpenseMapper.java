package com.rahul.smart_expense_tracker.mapper;

import com.rahul.smart_expense_tracker.dto.request.ExpenseRequest;
import com.rahul.smart_expense_tracker.dto.response.ExpenseResponse;
import com.rahul.smart_expense_tracker.entity.Category;
import com.rahul.smart_expense_tracker.entity.Expense;
import com.rahul.smart_expense_tracker.entity.Tag;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.enums.ExpenseStatus;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ExpenseMapper {

    public Expense toEntity(ExpenseRequest request, User user, Category category, Set<Tag> tags){
        return Expense.builder()
                .amount(request.getAmount())
                .description(request.getDescription().trim())
                .note(request.getNote())
                .expenseDate(request.getExpenseDate())
                .paymentMethod(request.getPaymentMethod())
                .receiptUrl(request.getReceiptUrl())
                .status(request.getStatus() != null ? request.getStatus() : ExpenseStatus.CONFIRMED)
                .user(user)
                .category(category)
                .tags(tags != null ? tags : new HashSet<>())
                .build();
    }

    public ExpenseResponse toResponse(Expense expense) {
        return ExpenseResponse.builder()
                .expenseId(expense.getExpenseId())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .note(expense.getNote())
                .expenseDate(expense.getExpenseDate())
                .paymentMethod(expense.getPaymentMethod() != null ? expense.getPaymentMethod().name() : null)
                .receiptUrl(expense.getReceiptUrl())
                .status(expense.getStatus().name())
                .categoryId(expense.getCategory().getCategoryId())
                .categoryName(expense.getCategory().getName())
                .categoryIcon(expense.getCategory().getIcon())
                .tags(expense.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()))
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
    public List<ExpenseResponse> toResponseList(List<Expense> expenses) {
        return expenses.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Update existing entity from request
    public void updateEntity(Expense expense, ExpenseRequest request, Category category, Set<Tag> tags) {
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription().trim());
        expense.setNote(request.getNote());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setPaymentMethod(request.getPaymentMethod());
        expense.setReceiptUrl(request.getReceiptUrl());
        if (request.getStatus() != null) {
            expense.setStatus(request.getStatus());
        }
        expense.setCategory(category);
        expense.setTags(tags != null ? tags : new HashSet<>());
    }



}
