package com.expensetracker.service;

import com.expensetracker.dto.CategorySummaryResponse;
import com.expensetracker.dto.ReportResponse;
import com.expensetracker.dto.TransactionResponse;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public ReportService(TransactionRepository transactionRepository, TransactionService transactionService) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    public ReportResponse generateReport(Long userId, String period, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate;
        LocalDate end = endDate;
        LocalDate now = LocalDate.now();

        if (period != null) {
            switch (period.toUpperCase()) {
                case "DAILY":
                    start = now;
                    end = now;
                    break;
                case "WEEKLY":
                    start = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    end = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                    break;
                case "MONTHLY":
                    start = now.withDayOfMonth(1);
                    end = now.with(TemporalAdjusters.lastDayOfMonth());
                    break;
                case "YEARLY":
                    start = now.withDayOfYear(1);
                    end = now.with(TemporalAdjusters.lastDayOfYear());
                    break;
            }
        }

        if (start == null || end == null) {
            start = now.withDayOfMonth(1);
            end = now.with(TemporalAdjusters.lastDayOfMonth());
        }

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, start, end);
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(t.getAmount());
            } else if (t.getType() == TransactionType.EXPENSE) {
                totalExpense = totalExpense.add(t.getAmount());
            }
        }

        BigDecimal balance = totalIncome.subtract(totalExpense);

        List<TransactionResponse> txResponses = transactions.stream()
                .map(transactionService::mapToResponse)
                .collect(Collectors.toList());

        List<CategorySummaryResponse> categorySummaries = calculateCategorySummaries(transactions, totalExpense);

        return new ReportResponse(totalIncome, totalExpense, balance, categorySummaries, txResponses);
    }

    private List<CategorySummaryResponse> calculateCategorySummaries(List<Transaction> transactions, BigDecimal totalExpense) {
        List<CategorySummaryResponse> summaries = new ArrayList<>();
        if (totalExpense.compareTo(BigDecimal.ZERO) == 0) return summaries;

        Map<String, BigDecimal> categoryTotals = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.mapping(Transaction::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        for (Map.Entry<String, BigDecimal> entry : categoryTotals.entrySet()) {
            double percentage = entry.getValue().divide(totalExpense, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            summaries.add(new CategorySummaryResponse(entry.getKey(), entry.getValue(), percentage));
        }

        return summaries;
    }
}
