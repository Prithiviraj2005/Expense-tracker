package com.expensetracker.service;

import com.expensetracker.dto.CategorySummaryResponse;
import com.expensetracker.dto.DashboardSummaryResponse;
import com.expensetracker.dto.MonthlySummaryResponse;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getSummary_ReturnsCorrectCalculations() {
        when(transactionRepository.sumAmountByUserIdAndType(1L, TransactionType.INCOME))
                .thenReturn(new BigDecimal("5000.00"));
        when(transactionRepository.sumAmountByUserIdAndType(1L, TransactionType.EXPENSE))
                .thenReturn(new BigDecimal("2000.00"));
        when(transactionRepository.sumAmountByUserIdAndTypeAndDateRange(eq(1L), eq(TransactionType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("3000.00"));
        when(transactionRepository.sumAmountByUserIdAndTypeAndDateRange(eq(1L), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("1000.00"));
        when(transactionRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        DashboardSummaryResponse summary = dashboardService.getSummary(1L);

        assertEquals(new BigDecimal("5000.00"), summary.getTotalIncome());
        assertEquals(new BigDecimal("2000.00"), summary.getTotalExpense());
        assertEquals(new BigDecimal("3000.00"), summary.getBalance());
    }

    @Test
    void getSummary_WithNoTransactions_ReturnsZeros() {
        when(transactionRepository.sumAmountByUserIdAndType(1L, TransactionType.INCOME))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumAmountByUserIdAndType(1L, TransactionType.EXPENSE))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumAmountByUserIdAndTypeAndDateRange(eq(1L), eq(TransactionType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumAmountByUserIdAndTypeAndDateRange(eq(1L), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        DashboardSummaryResponse summary = dashboardService.getSummary(1L);

        assertEquals(BigDecimal.ZERO, summary.getTotalIncome());
        assertEquals(BigDecimal.ZERO, summary.getTotalExpense());
        assertEquals(BigDecimal.ZERO, summary.getBalance());
    }

    @Test
    void getCategorySummary_WithData_ReturnsCategorySummaries() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{"Food", new BigDecimal("500.00")});
        mockResults.add(new Object[]{"Transport", new BigDecimal("300.00")});

        when(transactionRepository.findCategorySummaryByUserIdAndType(1L, TransactionType.EXPENSE))
                .thenReturn(mockResults);
        when(transactionRepository.sumAmountByUserIdAndType(1L, TransactionType.EXPENSE))
                .thenReturn(new BigDecimal("800.00"));

        List<CategorySummaryResponse> summaries = dashboardService.getCategorySummary(1L, TransactionType.EXPENSE);

        assertFalse(summaries.isEmpty());
        assertEquals(2, summaries.size());
        assertEquals("Food", summaries.get(0).getCategory());
    }

    @Test
    void getMonthlySummary_ReturnsMonthlyBreakdown() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1, new BigDecimal("5000.00"), new BigDecimal("2000.00")});
        mockResults.add(new Object[]{2, new BigDecimal("4000.00"), new BigDecimal("1500.00")});

        when(transactionRepository.findMonthlySummaryByUserIdAndYear(1L, 2026))
                .thenReturn(mockResults);

        List<MonthlySummaryResponse> summaries = dashboardService.getMonthlySummary(1L, 2026);

        assertNotNull(summaries);
        assertEquals(2, summaries.size());
        assertEquals("Jan", summaries.get(0).getMonth());
    }
}
