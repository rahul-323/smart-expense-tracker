package com.rahul.smart_expense_tracker.service.impl;

import com.rahul.smart_expense_tracker.dto.request.BudgetRequest;
import com.rahul.smart_expense_tracker.dto.response.BudgetResponse;
import com.rahul.smart_expense_tracker.dto.response.BudgetStatusResponse;
import com.rahul.smart_expense_tracker.entity.Budget;
import com.rahul.smart_expense_tracker.entity.Category;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.exception.BadRequestException;
import com.rahul.smart_expense_tracker.exception.DuplicateResourceException;
import com.rahul.smart_expense_tracker.exception.ResourceNotFoundException;
import com.rahul.smart_expense_tracker.mapper.BudgetMapper;
import com.rahul.smart_expense_tracker.repository.BudgetRepository;
import com.rahul.smart_expense_tracker.repository.CategoryRepository;
import com.rahul.smart_expense_tracker.repository.ExpenseRepository;
import com.rahul.smart_expense_tracker.repository.UserRepository;
import com.rahul.smart_expense_tracker.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetMapper budgetMapper;

    @Override
    public BudgetResponse createBudget(String email, BudgetRequest request) {
        User user = getUserByEmail(email);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        boolean duplicate = budgetRepository
                .findByUserUserIdAndMonthAndYear(user.getUserId(), request.getMonth(), request.getYear())
                .stream()
                .anyMatch(b -> b.getCategory().getCategoryId().equals(request.getCategoryId()));

        if (duplicate) {
            throw new DuplicateResourceException(
                    "A budget already exists for this category in the selected month/year");
        }

        YearMonth yearMonth = YearMonth.of(request.getYear(), request.getMonth());

        Budget budget = Budget.builder()
                .budgetLimit(request.getBudgetLimit())
                .month(request.getMonth())
                .year(request.getYear())
                .startDate(yearMonth.atDay(1))
                .endDate(yearMonth.atEndOfMonth())
                .alertThreshold(request.getAlertThreshold() != null ? request.getAlertThreshold() : 80)
                .user(user)
                .category(category)
                .build();

        return budgetMapper.toResponse(budgetRepository.save(budget));
    }

    @Override
    public List<BudgetResponse> getAllBudgets(String email) {
        User user = getUserByEmail(email);
        LocalDate now = LocalDate.now();

        return budgetRepository
                .findByUserUserIdAndMonthAndYear(user.getUserId(), now.getMonthValue(), now.getYear())
                .stream()
                .map(budgetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BudgetResponse getBudgetById(String email, Long budgetId) {
        User user = getUserByEmail(email);
        Budget budget = getOwnedBudget(budgetId, user);
        return budgetMapper.toResponse(budget);
    }

    @Override
    public List<BudgetResponse> getBudgetsForMonth(String email, Integer year, Integer month) {
        User user = getUserByEmail(email);

        return budgetRepository.findByUserUserIdAndMonthAndYear(user.getUserId(), month, year)
                .stream()
                .map(budgetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BudgetResponse updateBudget(String email, Long budgetId, BudgetRequest request) {
        User user = getUserByEmail(email);
        Budget budget = getOwnedBudget(budgetId, user);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        YearMonth yearMonth = YearMonth.of(request.getYear(), request.getMonth());

        budget.setBudgetLimit(request.getBudgetLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setStartDate(yearMonth.atDay(1));
        budget.setEndDate(yearMonth.atEndOfMonth());
        budget.setAlertThreshold(
                request.getAlertThreshold() != null ? request.getAlertThreshold() : budget.getAlertThreshold());
        budget.setCategory(category);

        return budgetMapper.toResponse(budgetRepository.save(budget));
    }

    @Override
    public void deleteBudget(String email, Long budgetId) {
        User user = getUserByEmail(email);
        Budget budget = getOwnedBudget(budgetId, user);
        budgetRepository.delete(budget);
    }

    @Override
    public List<BudgetStatusResponse> getBudgetStatus(String email) {
        User user = getUserByEmail(email);
        LocalDate now = LocalDate.now();

        List<Budget> budgets = budgetRepository
                .findByUserUserIdAndMonthAndYear(user.getUserId(), now.getMonthValue(), now.getYear());

        return budgets.stream()
                .map(budget -> buildBudgetStatus(budget, user))
                .collect(Collectors.toList());
    }

    private BudgetStatusResponse buildBudgetStatus(Budget budget, User user) {
        BigDecimal spent = expenseRepository.sumAmountByUserCategoryAndDateRange(
                user.getUserId(),
                budget.getCategory().getCategoryId(),
                budget.getStartDate(),
                budget.getEndDate());

        if (spent == null) {
            spent = BigDecimal.ZERO;
        }

        BigDecimal limit = budget.getBudgetLimit();
        BigDecimal remaining = limit.subtract(spent);

        double percentUsed = 0.0;
        if (limit.compareTo(BigDecimal.ZERO) > 0) {
            percentUsed = spent
                    .divide(limit, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        String status;
        if (percentUsed >= 100) {
            status = "EXCEEDED";
        } else if (percentUsed >= budget.getAlertThreshold()) {
            status = "WARNING";
        } else {
            status = "ON_TRACK";
        }

        return BudgetStatusResponse.builder()
                .budgetId(budget.getBudgetId())
                .categoryId(budget.getCategory().getCategoryId())
                .categoryName(budget.getCategory().getName())
                .categoryIcon(budget.getCategory().getIcon())
                .categoryColor(budget.getCategory().getColor())
                .budgetLimit(limit)
                .spentAmount(spent)
                .remainingAmount(remaining)
                .percentUsed(percentUsed)
                .status(status)
                .month(budget.getMonth())
                .year(budget.getYear())
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private Budget getOwnedBudget(Long budgetId, User user) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));

        if (!budget.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("You are not authorized to access this budget");
        }

        return budget;
    }
}