package com.expensetracker.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private BigDecimal currentMonthIncome;
    private BigDecimal currentMonthExpense;
    private List<TransactionResponse> recentTransactions;

    public DashboardSummaryResponse() {}

    public DashboardSummaryResponse(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal balance, BigDecimal currentMonthIncome, BigDecimal currentMonthExpense, List<TransactionResponse> recentTransactions) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
        this.currentMonthIncome = currentMonthIncome;
        this.currentMonthExpense = currentMonthExpense;
        this.recentTransactions = recentTransactions;
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

    public BigDecimal getCurrentMonthIncome() {
        return currentMonthIncome;
    }

    public void setCurrentMonthIncome(BigDecimal currentMonthIncome) {
        this.currentMonthIncome = currentMonthIncome;
    }

    public BigDecimal getCurrentMonthExpense() {
        return currentMonthExpense;
    }

    public void setCurrentMonthExpense(BigDecimal currentMonthExpense) {
        this.currentMonthExpense = currentMonthExpense;
    }

    public List<TransactionResponse> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionResponse> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}
