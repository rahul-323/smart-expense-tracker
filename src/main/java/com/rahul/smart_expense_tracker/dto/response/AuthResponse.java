package com.rahul.smart_expense_tracker.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String tokenType="Bearer";
    private String userId;
    private String name;
    private String email;
    private String role;
}
