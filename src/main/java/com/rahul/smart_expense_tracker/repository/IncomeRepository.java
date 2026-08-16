package com.rahul.smart_expense_tracker.repository;

import com.rahul.smart_expense_tracker.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i " +
            "WHERE i.user.userId = :userId " +
            "AND i.incomeDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
