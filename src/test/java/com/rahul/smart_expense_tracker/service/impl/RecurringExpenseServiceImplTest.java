package com.rahul.smart_expense_tracker.service.impl;

import com.rahul.smart_expense_tracker.entity.RecurringExpense;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.entity.Category;
import com.rahul.smart_expense_tracker.repository.ExpenseRepository;
import com.rahul.smart_expense_tracker.repository.RecurringExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecurringExpenseServiceImplTest {

    @Mock
    private RecurringExpenseRepository recurringExpenseRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private RecurringExpenseServiceImpl recurringExpenseService;

    @Test
    void processDueRecurringExpenses_savesExpenseAndUpdatesNextRun() {
        RecurringExpense r = RecurringExpense.builder()
                .recurringId(1L)
                .amount(new BigDecimal("100.00"))
                .description("Monthly subscription")
                .nextRunDate(LocalDate.now())
                .frequency(null)
                .interval(1)
                .active(true)
                .user(User.builder().userId(2L).build())
                .category(Category.builder().categoryId(3L).build())
                .build();

        when(recurringExpenseRepository.findByActiveTrueAndNextRunDateLessThanEqual(LocalDate.now()))
                .thenReturn(List.of(r));

        recurringExpenseService.processDueRecurringExpenses();

        verify(expenseRepository).save(org.mockito.ArgumentMatchers.any());
        verify(recurringExpenseRepository).save(org.mockito.ArgumentMatchers.any(RecurringExpense.class));
    }
}
