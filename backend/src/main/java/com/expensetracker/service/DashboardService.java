package com.expensetracker.service;

import com.expensetracker.dto.CategorySummaryResponse;
import com.expensetracker.dto.DashboardSummaryResponse;
import com.expensetracker.dto.MonthlySummaryResponse;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.repository.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public DashboardService(TransactionRepository transactionRepository, TransactionService transactionService) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    public DashboardSummaryResponse getSummary(Long userId) {
        BigDecimal totalIncome = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.INCOME);
        BigDecimal totalExpense = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE);
        BigDecimal balance = totalIncome.subtract(totalExpense);

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = YearMonth.from(now).atEndOfMonth();

        BigDecimal currentMonthIncome = transactionRepository.sumAmountByUserIdAndTypeAndDateRange(userId, TransactionType.INCOME, startOfMonth, endOfMonth);
        BigDecimal currentMonthExpense = transactionRepository.sumAmountByUserIdAndTypeAndDateRange(userId, TransactionType.EXPENSE, startOfMonth, endOfMonth);

        var recentPage = transactionRepository.findByUserId(userId, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "transactionDate")));
        var recentTransactions = recentPage.getContent().stream().map(transactionService::mapToResponse).collect(Collectors.toList());

        return new DashboardSummaryResponse(totalIncome, totalExpense, balance, currentMonthIncome, currentMonthExpense, recentTransactions);
    }

    public List<CategorySummaryResponse> getCategorySummary(Long userId, TransactionType type) {
        List<Object[]> results = transactionRepository.findCategorySummaryByUserIdAndType(userId, type);
        BigDecimal total = transactionRepository.sumAmountByUserIdAndType(userId, type);

        List<CategorySummaryResponse> summary = new ArrayList<>();
        if (total.compareTo(BigDecimal.ZERO) == 0) return summary;

        for (Object[] result : results) {
            String category = (String) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            double percentage = amount.divide(total, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            summary.add(new CategorySummaryResponse(category, amount, percentage));
        }

        return summary;
    }

    public List<MonthlySummaryResponse> getMonthlySummary(Long userId, int year) {
        List<Object[]> results = transactionRepository.findMonthlySummaryByUserIdAndYear(userId, year);
        List<MonthlySummaryResponse> summary = new ArrayList<>();

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        for (Object[] result : results) {
            int monthIndex = (int) result[0] - 1;
            BigDecimal income = (BigDecimal) result[1];
            BigDecimal expense = (BigDecimal) result[2];
            summary.add(new MonthlySummaryResponse(months[monthIndex], income, expense));
        }

        return summary;
    }
}
