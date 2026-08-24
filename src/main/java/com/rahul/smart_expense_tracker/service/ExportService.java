package com.rahul.smart_expense_tracker.service;


public interface ExportService {

    byte[] exportAllExpenses(String email);


    // Export expenses for a specific month
    byte[] exportExpensesForMonth(Integer year, Integer month, String email);

}
