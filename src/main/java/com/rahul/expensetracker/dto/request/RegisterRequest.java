package com.rahul.expensetracker.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be 2-50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8,max = 50,message = "Password must at least be 8 characters")
    private String password;

    @NotBlank(message = "Phone is required")
    @Size(min = 8,max = 50,message = "Password must at least be 8 characters")
    private String phone;

    private BigDecimal monthlyIncome;
}
