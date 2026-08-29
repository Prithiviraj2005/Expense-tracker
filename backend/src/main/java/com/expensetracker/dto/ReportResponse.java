package com.expensetracker.dto;

import java.math.BigDecimal;
import java.util.List;

public class ReportResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private List<CategorySummaryResponse> categorySummaries;
    private List<TransactionResponse> transactions;

    public ReportResponse() {}

    public ReportResponse(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal balance, List<CategorySummaryResponse> categorySummaries, List<TransactionResponse> transactions) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
        this.categorySummaries = categorySummaries;
        this.transactions = transactions;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<CategorySummaryResponse> getCategorySummaries() {
        return categorySummaries;
    }

    public void setCategorySummaries(List<CategorySummaryResponse> categorySummaries) {
        this.categorySummaries = categorySummaries;
    }

    public List<TransactionResponse> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionResponse> transactions) {
        this.transactions = transactions;
    }
}
