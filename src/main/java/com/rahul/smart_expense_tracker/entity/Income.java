package com.rahul.smart_expense_tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "incomes", indexes = {
        @Index(name = "idx_income_user", columnList = "user_user_id"),
        @Index(name = "idx_income_date", columnList = "income_date")
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incomeId;

    @NotNull(message = "Income amount is required")
    @DecimalMin(value = "0.01", message = "Income amount must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "Income date is required")
    @Column(name = "income_date", nullable = false)
    private LocalDate incomeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_user_id", nullable = false)
    private User user;

    private String source;
    private String note;
}
