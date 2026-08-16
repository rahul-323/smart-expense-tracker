package com.rahul.smart_expense_tracker.dto.request;

import com.rahul.smart_expense_tracker.enums.RecurrenceFrequency;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringExpenseRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(max = 255)
    private String description;

    @Size(max = 500)
    private String note;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Frequency is required")
    private RecurrenceFrequency frequency;

    @Min(value = 1)
    private Integer interval;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

}
