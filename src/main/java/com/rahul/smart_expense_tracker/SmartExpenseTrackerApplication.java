package com.rahul.smart_expense_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SmartExpenseTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartExpenseTrackerApplication.class, args);
	}

}
