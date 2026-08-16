package com.rahul.smart_expense_tracker.repository;

import com.rahul.smart_expense_tracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserUserIdAndMonthAndYear(Long userId, Integer month, Integer year);
}
