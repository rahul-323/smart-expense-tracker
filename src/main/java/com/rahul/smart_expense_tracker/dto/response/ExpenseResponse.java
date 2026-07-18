package com.rahul.smart_expense_tracker.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {

    private Long expenseId;
    private BigDecimal amount;
    private String description;
    private String note;
    private LocalDate expenseDate;
    private String paymentMethod;
    private String receiptUrl;
    private String status;

    // Embedded category info (no need for separate call)
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;

    private List<String> tags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
