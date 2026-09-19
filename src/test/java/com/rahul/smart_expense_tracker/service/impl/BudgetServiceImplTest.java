package com.rahul.smart_expense_tracker.service.impl;


import com.rahul.smart_expense_tracker.dto.response.BudgetStatusResponse;
import com.rahul.smart_expense_tracker.entity.Budget;
import com.rahul.smart_expense_tracker.entity.Category;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.enums.CategoryType;
import com.rahul.smart_expense_tracker.repository.BudgetRepository;
import com.rahul.smart_expense_tracker.repository.CategoryRepository;
import com.rahul.smart_expense_tracker.repository.ExpenseRepository;
import com.rahul.smart_expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Budget Service Unit Tests")
public class BudgetServiceImplTest {


    @Mock private BudgetRepository budgetRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private ExpenseRepository expenseRepository;
    @Mock private com.rahul.smart_expense_tracker.mapper.BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    private User testUser;
    private Category foodCategory;
    private Budget foodBudget;
    private final String EMAIL = "rahul@gmail.com";

    @BeforeEach
    void setUp() {
        testUser = User.builder().userId(1L).email(EMAIL).build();

        foodCategory = Category.builder()
                .categoryId(1L)
                .name("Food & Dining")
                .icon("🍔")
                .color("#FF6B6B")
                .categoryType(CategoryType.DEFAULT)
                .build();

        LocalDate today = LocalDate.now();
        foodBudget = Budget.builder()
                .budgetId(1L)
                .budgetLimit(new BigDecimal("5000.00"))
                .month(today.getMonthValue())
                .year(today.getYear())
                .alertThreshold(80)
                .user(testUser)
                .category(foodCategory)
                .build();
    }

    @Test
    @DisplayName("Budget status should be ON_TRACK when spent below threshold")
    void getBudgetStatus_OnTrack() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(budgetRepository.findByUserUserIdAndMonthAndYear(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(foodBudget));
// Spent 2000 out of 5000 = 40% → ON_TRACK
        when(expenseRepository.sumAmountByUserCategoryAndDateRange(anyLong(), anyLong(), any(), any()))
                .thenReturn(new BigDecimal("2000.00"));

        List<BudgetStatusResponse> result = budgetService.getBudgetStatus(EMAIL);

        assertThat(result).hasSize(1);
        BudgetStatusResponse status = result.get(0);
        assertThat(status.getSpentAmount()).isEqualByComparingTo("2000.00");
        assertThat(status.getRemainingAmount()).isEqualByComparingTo("3000.00");
        assertThat(status.getPercentUsed()).isEqualTo(40.00);
        assertThat(status.getStatus()).isEqualTo("ON_TRACK");
    }

    @Test
    @DisplayName("Budget status should be WARNING when spent crosses threshold")
    void getBudgetStatus_Warning() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(budgetRepository.findByUserUserIdAndMonthAndYear(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(foodBudget));
// Spent 4500 out of 5000 = 90% → WARNING (threshold 80%)
        when(expenseRepository.sumAmountByUserCategoryAndDateRange(anyLong(), anyLong(), any(), any()))
                .thenReturn(new BigDecimal("4500.00"));

        List<BudgetStatusResponse> result = budgetService.getBudgetStatus(EMAIL);

        assertThat(result.get(0).getStatus()).isEqualTo("WARNING");
        assertThat(result.get(0).getPercentUsed()).isEqualTo(90.00);
    }

    @Test
    @DisplayName("Budget status should be EXCEEDED when spent exceeds limit")
    void getBudgetStatus_Exceeded() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(budgetRepository.findByUserUserIdAndMonthAndYear(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(foodBudget));
// Spent 6000 out of 5000 = 120% → EXCEEDED
        when(expenseRepository.sumAmountByUserCategoryAndDateRange(anyLong(), anyLong(), any(), any()))
                .thenReturn(new BigDecimal("6000.00"));

        List<BudgetStatusResponse> result = budgetService.getBudgetStatus(EMAIL);

        assertThat(result.get(0).getStatus()).isEqualTo("EXCEEDED");
        assertThat(result.get(0).getRemainingAmount()).isEqualByComparingTo("-1000.00");
    }

    @Test
    @DisplayName("Budget status should handle zero spending gracefully")
    void getBudgetStatus_ZeroSpending() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        when(budgetRepository.findByUserUserIdAndMonthAndYear(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(foodBudget));
        when(expenseRepository.sumAmountByUserCategoryAndDateRange(anyLong(), anyLong(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        List<BudgetStatusResponse> result = budgetService.getBudgetStatus(EMAIL);

        assertThat(result.get(0).getPercentUsed()).isEqualTo(0.00);
        assertThat(result.get(0).getStatus()).isEqualTo("ON_TRACK");
    }

}
