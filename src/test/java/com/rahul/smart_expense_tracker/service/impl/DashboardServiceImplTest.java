package com.rahul.smart_expense_tracker.service.impl;

import com.rahul.smart_expense_tracker.dto.response.DashboardSummaryResponse;
import com.rahul.smart_expense_tracker.entity.Budget;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.repository.BudgetRepository;
import com.rahul.smart_expense_tracker.repository.ExpenseRepository;
import com.rahul.smart_expense_tracker.repository.IncomeRepository;
import com.rahul.smart_expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void getSummary_shouldUseIncomeAndBudgetTotals() {
        User user = User.builder()
                .userId(1L)
                .email("user@example.com")
                .build();

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(incomeRepository.sumAmountByUserAndDateRange(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("2500.00"));
        when(expenseRepository.sumAmountByUserAndDateRange(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("1200.00"));
        when(budgetRepository.findByUserUserIdAndMonthAndYear(eq(1L), anyInt(), anyInt()))
                .thenReturn(List.of(Budget.builder().budgetLimit(new BigDecimal("2000.00")).build()));
        when(expenseRepository.countByUserUserIdAndExpenseDateBetween(eq(1L), any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(3L);
        when(expenseRepository.getCategoryWiseBreakdown(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.<Object[]>of(new Object[] { "Food", "🍔", "#FF6B6B", new BigDecimal("1000.00"), 2L }));

        DashboardSummaryResponse response = dashboardService.getSummary("user@example.com");

        assertEquals(new BigDecimal("2500.00"), response.getTotalIncome());
        assertEquals(new BigDecimal("1200.00"), response.getTotalExpense());
        assertEquals(new BigDecimal("2000.00"), response.getTotalBudgetLimit());
    }
}
