package com.rahul.smart_expense_tracker.controller;


import com.rahul.smart_expense_tracker.security.SecurityConfig;
import com.rahul.smart_expense_tracker.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("api/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    private String getCurrentUserByEmail(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCSV(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ){
        String email=getCurrentUserByEmail();

        byte csvData[];

        String fileName;


        if(month!=null && year !=null){
            csvData=exportService.exportExpensesForMonth(year,month,email);

            fileName=String.format("expenses_%d_%02d.csv",year,month);
        }else {
            // Otherwise → export everything
            csvData = exportService.exportAllExpenses(email);
            fileName = "expenses_all_" + LocalDate.now() + ".csv";
        }

        HttpHeaders headers=new HttpHeaders();

        headers.setContentType(MediaType.parseMediaType("text/csv"));

        headers.setContentDispositionFormData("attachment",fileName);

        headers.setCacheControl("no-cache, no-store, must-revalidate");

        return ResponseEntity.ok().headers(headers).body(csvData);

    }
}
