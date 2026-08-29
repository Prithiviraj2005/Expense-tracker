package com.expensetracker.service;

import com.expensetracker.dto.TransactionRequest;
import com.expensetracker.dto.TransactionResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Category category;
    private Transaction transaction;
    private TransactionRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        category = new Category();
        category.setId(1L);
        category.setName("Food");
        category.setType(TransactionType.EXPENSE);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setCategory(category);
        transaction.setUser(user);
        transaction.setCreatedAt(LocalDateTime.now());

        request = new TransactionRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setType(TransactionType.EXPENSE);
        request.setCategoryId(1L);
        request.setTransactionDate(LocalDate.now());
    }

    @Test
    void createTransaction_WithValidData_ReturnsTransactionResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.createTransaction(1L, request);

        assertNotNull(response);
        assertEquals(transaction.getId(), response.getId());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_WithInvalidCategory_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.createTransaction(1L, request));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void getTransaction_WithValidIdAndUserId_ReturnsTransactionResponse() {
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(transaction));

        TransactionResponse response = transactionService.getTransaction(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getTransaction_WithWrongUserId_ThrowsResourceNotFoundException() {
        when(transactionRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransaction(2L, 1L));
    }

    @Test
    void updateTransaction_WithValidOwnership_ReturnsUpdatedResponse() {
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(transaction));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse response = transactionService.updateTransaction(1L, 1L, request);

        assertNotNull(response);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deleteTransaction_WithOwnership_DeletesTransaction() {
        when(transactionRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(1L, 1L);

        verify(transactionRepository).delete(transaction);
    }

    @Test
    void deleteTransaction_WithWrongUserId_ThrowsResourceNotFoundException() {
        when(transactionRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.deleteTransaction(2L, 1L));
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }
}
