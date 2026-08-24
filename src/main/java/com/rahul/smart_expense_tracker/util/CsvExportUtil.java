package com.rahul.smart_expense_tracker.util;


import com.opencsv.CSVWriter;
import com.rahul.smart_expense_tracker.entity.Expense;
import com.rahul.smart_expense_tracker.entity.Tag;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CsvExportUtil {

    private static final DateTimeFormatter DATE_TIME_FORMAT=DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String[] HEADERS={
            "S.No",
            "Date",
            "Description",
            "Category",
            "Amount (INR)",
            "Payment Method",
            "Status",
            "Tags",
            "Note"

    };

    public byte[] exportExpenseToCsv(List<Expense> expenses){
        try(ByteArrayOutputStream out=new ByteArrayOutputStream();
            Writer writer=new OutputStreamWriter(out, StandardCharsets.UTF_8);
            CSVWriter csvWriter= new CSVWriter(writer)){
            csvWriter.writeNext(HEADERS);

            int serialNo=1;
            for(Expense expense:expenses){
                String tags=expense.getTags()!=null && !expense.getTags().isEmpty()
                        ? expense.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.joining("; "))
                        :"";


                String[] row = {
                        String.valueOf(serialNo++),
                        expense.getExpenseDate().format(DATE_TIME_FORMAT),
                        expense.getDescription(),
                        expense.getCategory().getName(),
                        expense.getAmount().toString(),
                        expense.getPaymentMethod() != null ? expense.getPaymentMethod().name() : "N/A",
                        expense.getStatus().name(),
                        tags,
                        expense.getNote() != null ? expense.getNote() : ""
                };

                csvWriter.writeNext(row);

            }
            csvWriter.flush();
            return out.toByteArray();

    } catch (IOException e) {
            throw new RuntimeException("Failed to export CSV of expenses: "+e.getMessage(),e);
        }

    }
}
