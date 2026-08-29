package com.expensetracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ExpenseTrackerApplicationTests {

    @Test
    void contextLoads() {
        // Simple test to ensure the Spring application context loads successfully
    }
}
