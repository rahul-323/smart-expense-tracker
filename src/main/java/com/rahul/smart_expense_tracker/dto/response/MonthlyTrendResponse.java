package com.rahul.smart_expense_tracker.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTrendResponse {

    private String monthLabel;      // "Jun 2026"
    private Integer month;
    private Integer year;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal savings;
}
