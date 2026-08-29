package com.expensetracker.dto;

import com.expensetracker.entity.PaymentMethod;
import com.expensetracker.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionRequest {
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    private String description;

    @NotNull
    private LocalDate transactionDate;

    private PaymentMethod paymentMethod;

    @NotNull
    private Long categoryId;

    public TransactionRequest() {}

    public TransactionRequest(BigDecimal amount, TransactionType type, String description, LocalDate transactionDate, PaymentMethod paymentMethod, Long categoryId) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.transactionDate = transactionDate;
        this.paymentMethod = paymentMethod;
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
