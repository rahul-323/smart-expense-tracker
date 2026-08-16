package com.rahul.smart_expense_tracker.service.impl;

import com.rahul.smart_expense_tracker.entity.Expense;
import com.rahul.smart_expense_tracker.entity.RecurringExpense;
import com.rahul.smart_expense_tracker.enums.RecurrenceFrequency;
import com.rahul.smart_expense_tracker.repository.RecurringExpenseRepository;
import com.rahul.smart_expense_tracker.repository.ExpenseRepository;
import com.rahul.smart_expense_tracker.service.RecurringExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import com.rahul.smart_expense_tracker.exception.ResourceNotFoundException;

@Service
@Transactional
public class RecurringExpenseServiceImpl implements RecurringExpenseService {
    private static final Logger logger = LoggerFactory.getLogger(RecurringExpenseServiceImpl.class);

    @Autowired
    private RecurringExpenseRepository recurringExpenseRepository;

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private com.rahul.smart_expense_tracker.repository.UserRepository userRepository;
    @Autowired
    private com.rahul.smart_expense_tracker.repository.CategoryRepository categoryRepository;

    @Override
    public void processDueRecurringExpenses() {
        LocalDate today = LocalDate.now();
        List<RecurringExpense> due = recurringExpenseRepository.findByActiveTrueAndNextRunDateLessThanEqual(today);
        if (due.isEmpty()) {
            logger.info("No recurring expenses due today");
            return;
        }

        for (RecurringExpense r : due) {
            try {
                // process possibly multiple missed runs (catch-up) but cap to avoid infinite
                // loops
                int safety = 0;
                LocalDate nextRun = r.getNextRunDate() != null ? r.getNextRunDate() : today;
                while (nextRun != null && !nextRun.isAfter(today) && r.getActive() && safety < 100) {
                    // if endDate exists and nextRun is after endDate, stop and deactivate
                    if (r.getEndDate() != null && nextRun.isAfter(r.getEndDate())) {
                        r.setActive(false);
                        break;
                    }

                    Expense e = Expense.builder()
                            .amount(r.getAmount())
                            .description(r.getDescription())
                            .note(r.getNote())
                            .expenseDate(nextRun)
                            .user(r.getUser())
                            .category(r.getCategory())
                            .build();

                    expenseRepository.save(e);

                    // advance
                    LocalDate computed = calculateNextRun(nextRun, r.getFrequency(), r.getInterval());
                    nextRun = computed;
                    r.setNextRunDate(nextRun);
                    safety++;
                }

                // If an end date was reached, and nextRun is after it, deactivate
                if (r.getEndDate() != null && r.getNextRunDate() != null
                        && r.getNextRunDate().isAfter(r.getEndDate())) {
                    r.setActive(false);
                }

                recurringExpenseRepository.save(r);
                logger.info("Processed recurring expense id={} nextRun={} active={}", r.getRecurringId(),
                        r.getNextRunDate(), r.getActive());
            } catch (Exception ex) {
                logger.error("Failed to process recurring expense id={}", r.getRecurringId(), ex);
            }
        }
    }

    @Override
    public com.rahul.smart_expense_tracker.dto.response.RecurringExpenseResponse createRecurring(
            com.rahul.smart_expense_tracker.dto.request.RecurringExpenseRequest request, String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        var category = categoryRepository.findCategoryByIdForUser(request.getCategoryId(), user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        RecurringExpense r = RecurringExpense.builder()
                .amount(request.getAmount())
                .description(request.getDescription())
                .note(request.getNote())
                .category(category)
                .user(user)
                .frequency(request.getFrequency())
                .interval(request.getInterval() != null ? request.getInterval() : 1)
                .startDate(request.getStartDate())
                .nextRunDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(true)
                .build();

        RecurringExpense saved = recurringExpenseRepository.save(r);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.rahul.smart_expense_tracker.dto.response.RecurringExpenseResponse> getAllForUser(
            String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        var list = recurringExpenseRepository.findByUserUserId(user.getUserId());
        return list.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public com.rahul.smart_expense_tracker.dto.response.RecurringExpenseResponse getById(Long id, String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        logger.debug("Fetching recurring id={} for user email={} userId={}", id, email, user.getUserId());
        var r = recurringExpenseRepository.findByRecurringIdAndUserUserId(id, user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("RecurringExpense", "id", id));
        return toResponse(r);
    }

    @Override
    public com.rahul.smart_expense_tracker.dto.response.RecurringExpenseResponse updateRecurring(Long id,
            com.rahul.smart_expense_tracker.dto.request.RecurringExpenseRequest request, String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        logger.debug("Updating recurring id={} for user email={} userId={}", id, email, user.getUserId());
        var r = recurringExpenseRepository.findByRecurringIdAndUserUserId(id, user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("RecurringExpense", "id", id));

        var category = categoryRepository.findCategoryByIdForUser(request.getCategoryId(), user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        r.setAmount(request.getAmount());
        r.setDescription(request.getDescription());
        r.setNote(request.getNote());
        r.setCategory(category);
        r.setFrequency(request.getFrequency());
        r.setInterval(request.getInterval() != null ? request.getInterval() : 1);
        r.setStartDate(request.getStartDate());
        r.setEndDate(request.getEndDate());

        RecurringExpense updated = recurringExpenseRepository.save(r);
        return toResponse(updated);
    }

    @Override
    public void toggleActive(Long id, String email, Boolean active) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        logger.debug("Toggling recurring id={} for user email={} userId={}", id, email, user.getUserId());
        var r = recurringExpenseRepository.findByRecurringIdAndUserUserId(id, user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("RecurringExpense", "id", id));
        r.setActive(active != null ? active : !r.getActive());
        recurringExpenseRepository.save(r);
    }

    @Override
    public void deleteRecurring(Long id, String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        logger.debug("Deleting recurring id={} for user email={} userId={}", id, email, user.getUserId());
        var r = recurringExpenseRepository.findByRecurringIdAndUserUserId(id, user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("RecurringExpense", "id", id));
        recurringExpenseRepository.delete(r);
    }

    private com.rahul.smart_expense_tracker.dto.response.RecurringExpenseResponse toResponse(RecurringExpense r) {
        return com.rahul.smart_expense_tracker.dto.response.RecurringExpenseResponse.builder()
                .recurringId(r.getRecurringId())
                .amount(r.getAmount())
                .description(r.getDescription())
                .note(r.getNote())
                .categoryId(r.getCategory() != null ? r.getCategory().getCategoryId() : null)
                .frequency(r.getFrequency())
                .interval(r.getInterval())
                .startDate(r.getStartDate())
                .nextRunDate(r.getNextRunDate())
                .endDate(r.getEndDate())
                .active(r.getActive())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    private LocalDate calculateNextRun(LocalDate base, RecurrenceFrequency frequency, Integer interval) {
        if (base == null)
            base = LocalDate.now();
        int inc = interval != null && interval > 0 ? interval : 1;
        if (frequency == null)
            frequency = RecurrenceFrequency.MONTHLY;

        return switch (frequency) {
            case DAILY -> base.plusDays(inc);
            case WEEKLY -> base.plusWeeks(inc);
            case MONTHLY -> base.plusMonths(inc);
            case YEARLY -> base.plusYears(inc);
        };
    }
}
