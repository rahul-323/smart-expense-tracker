package com.rahul.smart_expense_tracker.dto.response;

import com.rahul.smart_expense_tracker.enums.RecurrenceFrequency;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringExpenseResponse {
    private Long recurringId;
    private BigDecimal amount;
    private String description;
    private String note;
    private Long categoryId;
    private RecurrenceFrequency frequency;
    private Integer interval;
    private LocalDate startDate;
    private LocalDate nextRunDate;
    private LocalDate endDate;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
