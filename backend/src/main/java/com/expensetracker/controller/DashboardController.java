package com.expensetracker.controller;

import com.expensetracker.dto.CategorySummaryResponse;
import com.expensetracker.dto.DashboardSummaryResponse;
import com.expensetracker.dto.MonthlySummaryResponse;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.entity.User;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.DashboardService;
import com.expensetracker.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    public DashboardController(DashboardService dashboardService, UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
    }

    private Long getUserId() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email).orElseThrow();
        return user.getId();
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary(getUserId()));
    }

    @GetMapping("/category-summary")
    public ResponseEntity<List<CategorySummaryResponse>> getCategorySummary(
            @RequestParam(defaultValue = "EXPENSE") TransactionType type) {
        return ResponseEntity.ok(dashboardService.getCategorySummary(getUserId(), type));
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<List<MonthlySummaryResponse>> getMonthlySummary(
            @RequestParam(required = false) Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        return ResponseEntity.ok(dashboardService.getMonthlySummary(getUserId(), year));
    }
}
