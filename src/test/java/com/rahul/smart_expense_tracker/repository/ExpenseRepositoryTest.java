package com.rahul.smart_expense_tracker.repository;

import com.rahul.smart_expense_tracker.entity.Category;
import com.rahul.smart_expense_tracker.entity.Expense;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.enums.CategoryType;
import com.rahul.smart_expense_tracker.enums.ExpenseStatus;
import com.rahul.smart_expense_tracker.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // Use H2
@DisplayName("Expense Repository Tests")
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User savedUser;
    private Category savedCategory;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(User.builder()
                .name("Rahul")
                .email("rahul@test.com")
                .password("hashed@123")
                .userRole(UserRole.ROLE_USER)
                .currency("INR")
                .build());

        savedCategory = categoryRepository.save(Category.builder()
                .name("Food")
                .categoryType(CategoryType.DEFAULT)
                .isActive(true)
                .build());
    }

    @Test
    @DisplayName("Should sum expenses by user and date range")
    void sumAmountByUserAndDateRange_Success() {
// Create 3 expenses in June
        createExpense(new BigDecimal("100.00"), LocalDate.of(2026, 6, 5));
        createExpense(new BigDecimal("200.00"), LocalDate.of(2026, 6, 15));
        createExpense(new BigDecimal("300.00"), LocalDate.of(2026, 6, 25));
// One in July (should NOT be counted)
        createExpense(new BigDecimal("999.00"), LocalDate.of(2026, 7, 1));

        BigDecimal total = expenseRepository.sumAmountByUserAndDateRange(
                savedUser.getUserId(),
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );

        assertThat(total).isEqualByComparingTo("600.00"); // 100+200+300
    }

    @Test
    @DisplayName("Should return zero when no expenses in range")
    void sumAmountByUserAndDateRange_NoExpenses_ReturnsZero() {
        BigDecimal total = expenseRepository.sumAmountByUserAndDateRange(
                savedUser.getUserId(),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );

        assertThat(total).isEqualByComparingTo("0"); // COALESCE returns 0
    }

    @Test
    @DisplayName("Should count expenses in date range")
    void countByUserAndDateRange_Success() {
        createExpense(new BigDecimal("100.00"), LocalDate.of(2026, 6, 5));
        createExpense(new BigDecimal("200.00"), LocalDate.of(2026, 6, 15));

        Long count = expenseRepository.countByUserUserIdAndExpenseDateBetween(
                savedUser.getUserId(),
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        );

        assertThat(count).isEqualTo(2);
    }

    // Helper
    private void createExpense(BigDecimal amount, LocalDate date) {
        expenseRepository.save(Expense.builder()
                .amount(amount)
                .description("Test expense")
                .expenseDate(date)
                .status(ExpenseStatus.CONFIRMED)
                .user(savedUser)
                .category(savedCategory)
                .build());
    }
}