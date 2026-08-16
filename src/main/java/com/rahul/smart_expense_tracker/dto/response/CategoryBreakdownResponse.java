package com.rahul.smart_expense_tracker.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryBreakdownResponse {

    private String categoryName;
    private String categoryIcon;
    private String categoryColor;
    private BigDecimal amount;
    private Double percentage;      // this category's % of total spend
    private Long transactionCount;
}
