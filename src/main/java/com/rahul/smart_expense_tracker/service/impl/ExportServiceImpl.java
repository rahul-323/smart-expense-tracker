package com.rahul.smart_expense_tracker.service.impl;

import com.rahul.smart_expense_tracker.entity.Expense;
import com.rahul.smart_expense_tracker.entity.User;
import com.rahul.smart_expense_tracker.exception.ResourceNotFoundException;
import com.rahul.smart_expense_tracker.repository.ExpenseRepository;
import com.rahul.smart_expense_tracker.repository.UserRepository;
import com.rahul.smart_expense_tracker.service.ExportService;
import com.rahul.smart_expense_tracker.util.CsvExportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ExportServiceImpl implements ExportService {


    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CsvExportUtil csvExportUtil;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    public byte[] exportAllExpenses(String email) {
        User user= getUserByEmail(email);

        LocalDate startDate=LocalDate.of(2000,1,1);
        LocalDate endDate=LocalDate.of(2100,12,31);

        List<Expense> expenses=expenseRepository.
                findByUserUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(user.getUserId(),startDate,endDate);

        return csvExportUtil.exportExpenseToCsv(expenses);
    }

    @Override
    public byte[] exportExpensesForMonth(Integer year, Integer month, String email) {
        User user=getUserByEmail(email);

        YearMonth ym=YearMonth.of(year,month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Expense> expenses=expenseRepository.
                findByUserUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(user.getUserId(),start,end);


        return csvExportUtil.exportExpenseToCsv(expenses);

    }
}
