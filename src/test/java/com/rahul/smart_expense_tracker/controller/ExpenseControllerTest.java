package com.rahul.smart_expense_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.smart_expense_tracker.dto.request.ExpenseRequest;
import com.rahul.smart_expense_tracker.dto.response.ExpenseResponse;
import com.rahul.smart_expense_tracker.security.JwtAuthenticationFilter;
import com.rahul.smart_expense_tracker.service.ExpenseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ExpenseController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class))
@DisplayName("Expense Controller Tests")
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private ExpenseService expenseService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;

    @Test
    @WithMockUser(username = "rahul@gmail.com")
    @DisplayName("POST /api/expenses should create expense and return 201")
    void createExpense_Returns201() throws Exception {
        ExpenseRequest request = ExpenseRequest.builder()
                .amount(new BigDecimal("500.00"))
                .description("Lunch")
                .expenseDate(LocalDate.of(2026, 6, 15))
                .categoryId(1L)
                .build();

        ExpenseResponse response = ExpenseResponse.builder()
                .expenseId(1L)
                .amount(new BigDecimal("500.00"))
                .description("Lunch")
                .build();

        when(expenseService.createExpense(any(), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/expenses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.expenseId").value(1))
                .andExpect(jsonPath("$.data.description").value("Lunch"));
    }

    @Test
    @WithMockUser(username = "rahul@gmail.com")
    @DisplayName("POST /api/expenses with invalid data should return 400")
    void createExpense_InvalidData_Returns400() throws Exception {
        // Missing amount and date — validation should fail
        ExpenseRequest request = ExpenseRequest.builder()
                .description("Invalid expense")
                .build();

        mockMvc.perform(post("/api/expenses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}