package com.rahul.smart_expense_tracker.scheduler;

import com.rahul.smart_expense_tracker.service.RecurringExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RecurringExpenseScheduler {
    private static final Logger logger = LoggerFactory.getLogger(RecurringExpenseScheduler.class);

    private final RecurringExpenseService recurringExpenseService;

    @Autowired
    public RecurringExpenseScheduler(RecurringExpenseService recurringExpenseService) {
        this.recurringExpenseService = recurringExpenseService;
    }

    // Runs daily at 00:05 to avoid midnight edge cases
    @Scheduled(cron = "0 5 0 * * *")
    public void runDailyRecurringExpenseJob() {
        logger.info("RecurringExpenseScheduler started");
        try {
            recurringExpenseService.processDueRecurringExpenses();
            logger.info("RecurringExpenseScheduler completed successfully");
        } catch (Exception e) {
            logger.error("RecurringExpenseScheduler failed", e);
        }
    }
}
