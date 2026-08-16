package com.rahul.smart_expense_tracker.repository;

import com.rahul.smart_expense_tracker.entity.RecurringExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {
    List<RecurringExpense> findByActiveTrueAndNextRunDateLessThanEqual(LocalDate date);

    List<RecurringExpense> findByUserUserId(Long userId);

    java.util.Optional<RecurringExpense> findByRecurringIdAndUserUserId(Long recurringId, Long userId);
}
