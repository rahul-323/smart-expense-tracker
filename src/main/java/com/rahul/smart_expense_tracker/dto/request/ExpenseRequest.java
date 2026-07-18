package com.rahul.smart_expense_tracker.dto.request;

import com.rahul.smart_expense_tracker.enums.ExpenseStatus;
import com.rahul.smart_expense_tracker.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(max = 255)
    private String description;

    @Size(max = 500)
    private String note;

    @NotNull(message = "Expense date is required")
    @PastOrPresent(message = "Expense date cannot be in the future")
    private LocalDate expenseDate;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private PaymentMethod paymentMethod;

    private String receiptUrl;

    private ExpenseStatus status;

    private List<String> tagNames;     // ["trip", "office"] — auto-created if not exist

}
