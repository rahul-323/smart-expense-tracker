package com.rahul.smart_expense_tracker.repository;

import com.rahul.smart_expense_tracker.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long>, JpaSpecificationExecutor<Expense> {

    Page<Expense> findByUserUserId(Long userId, Pageable pageable);

    Optional<Expense> findByExpenseIdAndUserUserId(Long expenseId,Long userId);

    Page<Expense> findByUserUserIdAndDescriptionContainingIgnoreCase(Long userId, String keyword, Pageable pageable);

    Page<Expense> findByUserUserIdAndExpenseDateBetween(
            Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable
    );

    Page<Expense> findByUserUserIdAndCategoryCategoryId(
            Long userId, Long categoryId, Pageable pageable
    );


    // ── Sum expenses for user in date range (used by dashboard later) ──
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
            "WHERE e.user.userId = :userId " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate " +
            "AND e.status = 'CONFIRMED'")
    BigDecimal sumAmountByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ── Sum expenses for user, category, date range (used by budget later) ──
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
            "WHERE e.user.userId = :userId " +
            "AND e.category.categoryId = :categoryId " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate " +
            "AND e.status = 'CONFIRMED'")
    BigDecimal sumAmountByUserCategoryAndDateRange(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ── Count expenses in date range ──
    Long countByUserUserIdAndExpenseDateBetween(
            Long userId, LocalDate startDate, LocalDate endDate
    );

    // ── Get all expenses in date range (for export later) ──
    List<Expense> findByUserUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate
    );



}
