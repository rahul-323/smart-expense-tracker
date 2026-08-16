package com.rahul.smart_expense_tracker.service.impl;

import com.rahul.smart_expense_tracker.dto.response.*;
import com.rahul.smart_expense_tracker.entity.Budget;
import com.rahul.smart_expense_tracker.entity.Expense;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.exception.ResourceNotFoundException;
import com.rahul.smart_expense_tracker.mapper.ExpenseMapper;
import com.rahul.smart_expense_tracker.repository.BudgetRepository;
import com.rahul.smart_expense_tracker.repository.ExpenseRepository;
import com.rahul.smart_expense_tracker.repository.IncomeRepository;
import com.rahul.smart_expense_tracker.repository.UserRepository;
import com.rahul.smart_expense_tracker.service.DashboardService;
import com.rahul.smart_expense_tracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true) // All dashboard methods are read-only
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseMapper expenseMapper;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    // ─── SUMMARY (current month) ───
    @Override
    public DashboardSummaryResponse getSummary(String email) {
        LocalDate today = LocalDate.now();
        return buildSummary(email, today.getYear(), today.getMonthValue());
    }

    // ─── SUMMARY (specific month) ───
    @Override
    public DashboardSummaryResponse getSummaryForMonth(Integer year, Integer month, String email) {
        return buildSummary(email, year, month);
    }

    // ─── Core summary builder ───
    private DashboardSummaryResponse buildSummary(String email, int year, int month) {
        User user = getUserByEmail(email);
        Long userId = user.getUserId();

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // Total income and expense
        BigDecimal totalIncome = nullSafe(
                incomeRepository.sumAmountByUserAndDateRange(userId, start, end));
        BigDecimal totalExpense = nullSafe(
                expenseRepository.sumAmountByUserAndDateRange(userId, start, end));

        // Net savings
        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        // Savings percentage
        double savingsPercentage = 0.0;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsPercentage = netSavings
                    .divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        // Total budget limit (sum of all budgets this month)
        List<Budget> budgets = budgetRepository.findByUserUserIdAndMonthAndYear(userId, month, year);
        BigDecimal totalBudgetLimit = budgets.stream()
                .map(Budget::getBudgetLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Budget utilization
        double budgetUtilization = 0.0;
        if (totalBudgetLimit.compareTo(BigDecimal.ZERO) > 0) {
            budgetUtilization = totalExpense
                    .divide(totalBudgetLimit, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        // Expense count
        Long expenseCount = expenseRepository.countByUserUserIdAndExpenseDateBetween(userId, start, end);

        // Top category
        List<Object[]> breakdown = expenseRepository.getCategoryWiseBreakdown(userId, start, end);
        String topCategory = null;
        BigDecimal topCategoryAmount = BigDecimal.ZERO;
        if (!breakdown.isEmpty()) {
            Object[] top = breakdown.get(0); // already sorted DESC by amount
            topCategory = (String) top[0];
            topCategoryAmount = (BigDecimal) top[3];
        }

        String monthName = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        return DashboardSummaryResponse.builder()
                .monthName(monthName)
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netSavings(netSavings)
                .savingPercentage(savingsPercentage)
                .totalBudgetLimit(totalBudgetLimit)
                .totalBudgetSpent(totalExpense)
                .budgetUtilization(budgetUtilization)
                .expenseCount(expenseCount)
                .topCategory(topCategory)
                .topCategoryAmount(topCategoryAmount)
                .build();
    }

    // ─── CATEGORY BREAKDOWN (current month) ───
    @Override
    public List<CategoryBreakdownResponse> getCategoryBreakdown(String email) {
        LocalDate today = LocalDate.now();
        return buildCategoryBreakdown(email, today.getYear(), today.getMonthValue());
    }

    // ─── CATEGORY BREAKDOWN (specific month) ───
    @Override
    public List<CategoryBreakdownResponse> getCategoryBreakdownForMonth(Integer year, Integer month, String email) {
        return buildCategoryBreakdown(email, year, month);
    }

    private List<CategoryBreakdownResponse> buildCategoryBreakdown(String email, int year, int month) {
        User user = getUserByEmail(email);
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Object[]> rows = expenseRepository.getCategoryWiseBreakdown(user.getUserId(), start, end);

        // Calculate grand total for percentage
        BigDecimal grandTotal = rows.stream()
                .map(r -> (BigDecimal) r[3])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CategoryBreakdownResponse> result = new ArrayList<>();
        for (Object[] row : rows) {
            String name = (String) row[0];
            String icon = (String) row[1];
            String color = (String) row[2];
            BigDecimal amount = (BigDecimal) row[3];
            Long count = (Long) row[4];

            double percentage = 0.0;
            if (grandTotal.compareTo(BigDecimal.ZERO) > 0) {
                percentage = amount
                        .divide(grandTotal, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
            }

            result.add(CategoryBreakdownResponse.builder()
                    .categoryName(name)
                    .categoryIcon(icon)
                    .categoryColor(color)
                    .amount(amount)
                    .percentage(percentage)
                    .transactionCount(count)
                    .build());
        }
        return result;
    }

    // ─── MONTHLY TREND (last 6 months) ───
    @Override
    public List<MonthlyTrendResponse> getMonthlyTrend(String email) {
        User user = getUserByEmail(email);
        Long userId = user.getUserId();

        List<MonthlyTrendResponse> trend = new ArrayList<>();
        YearMonth current = YearMonth.now();

        // Loop from 5 months ago to current month
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = current.minusMonths(i);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            BigDecimal income = nullSafe(
                    incomeRepository.sumAmountByUserAndDateRange(userId, start, end));
            BigDecimal expense = nullSafe(
                    expenseRepository.sumAmountByUserAndDateRange(userId, start, end));
            BigDecimal savings = income.subtract(expense);

            String label = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    + " " + ym.getYear();

            trend.add(MonthlyTrendResponse.builder()
                    .monthLabel(label)
                    .month(ym.getMonthValue())
                    .year(ym.getYear())
                    .income(income)
                    .expense(expense)
                    .savings(savings)
                    .build());
        }
        return trend;
    }

    // ─── DAILY EXPENSES (for a month) ───
    @Override
    public List<DailyExpenseResponse> getDailyExpenses(Integer year, Integer month, String email) {
        User user = getUserByEmail(email);
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Object[]> rows = expenseRepository.getDailyExpenseTotals(user.getUserId(), start, end);

        List<DailyExpenseResponse> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(DailyExpenseResponse.builder()
                    .date((LocalDate) row[0])
                    .amount((BigDecimal) row[1])
                    .build());
        }
        return result;
    }

    // ─── TOP 5 EXPENSES (current month) ───
    @Override
    public List<ExpenseResponse> getTopExpenses(String email) {
        User user = getUserByEmail(email);
        LocalDate today = LocalDate.now();
        YearMonth ym = YearMonth.from(today);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        Pageable topFive = PageRequest.of(0, 5);
        List<Expense> topExpenses = expenseRepository.getTopExpenses(
                user.getUserId(), start, end, topFive);

        return expenseMapper.toResponseList(topExpenses);
    }

}
