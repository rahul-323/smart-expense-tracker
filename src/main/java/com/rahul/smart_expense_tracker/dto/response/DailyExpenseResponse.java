package com.rahul.smart_expense_tracker.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyExpenseResponse {

    private LocalDate date;
    private BigDecimal amount;
}
