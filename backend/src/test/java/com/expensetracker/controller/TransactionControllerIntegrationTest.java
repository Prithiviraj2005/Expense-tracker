package com.expensetracker.controller;

import com.expensetracker.dto.TransactionRequest;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.TransactionType;
import com.expensetracker.entity.PaymentMethod;
import com.expensetracker.entity.User;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User userA;
    private User userB;
    private Category testCategory;
    private Transaction userATransaction;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        userA = new User("User A", "usera@example.com", passwordEncoder.encode("password"));
        userA = userRepository.save(userA);

        userB = new User("User B", "userb@example.com", passwordEncoder.encode("password"));
        userB = userRepository.save(userB);

        testCategory = new Category();
        testCategory.setName("Food");
        testCategory.setType(TransactionType.EXPENSE);
        testCategory = categoryRepository.save(testCategory);

        userATransaction = new Transaction();
        userATransaction.setAmount(new BigDecimal("150.00"));
        userATransaction.setType(TransactionType.EXPENSE);
        userATransaction.setTransactionDate(LocalDate.now());
        userATransaction.setPaymentMethod(PaymentMethod.CASH);
        userATransaction.setDescription("Test expense");
        userATransaction.setCategory(testCategory);
        userATransaction.setUser(userA);
        userATransaction = transactionRepository.save(userATransaction);
    }

    private String getAuthToken(User user) {
        return "Bearer " + jwtTokenProvider.generateToken(user.getEmail());
    }

    @Test
    void createTransaction_WithValidAuth_Returns201() throws Exception {
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("100.00"), TransactionType.EXPENSE, "Lunch",
                LocalDate.now(), PaymentMethod.CASH, testCategory.getId());

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", getAuthToken(userA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createTransaction_WithAmountZero_Returns400() throws Exception {
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("0.00"), TransactionType.EXPENSE, "Bad",
                LocalDate.now(), PaymentMethod.CASH, testCategory.getId());

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", getAuthToken(userA))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTransaction_WithValidAuth_Returns200() throws Exception {
        mockMvc.perform(get("/api/transactions/" + userATransaction.getId())
                .header("Authorization", getAuthToken(userA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userATransaction.getId()));
    }

    // === CRITICAL SECURITY TESTS: USER DATA ISOLATION ===

    @Test
    void userBTriesToGetUserATransaction_Returns404() throws Exception {
        mockMvc.perform(get("/api/transactions/" + userATransaction.getId())
                .header("Authorization", getAuthToken(userB)))
                .andExpect(status().isNotFound());
    }

    @Test
    void userBTriesToUpdateUserATransaction_Returns404() throws Exception {
        TransactionRequest request = new TransactionRequest(
                new BigDecimal("200.00"), TransactionType.EXPENSE, "Updated",
                LocalDate.now(), PaymentMethod.UPI, testCategory.getId());

        mockMvc.perform(put("/api/transactions/" + userATransaction.getId())
                .header("Authorization", getAuthToken(userB))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void userBTriesToDeleteUserATransaction_Returns404() throws Exception {
        mockMvc.perform(delete("/api/transactions/" + userATransaction.getId())
                .header("Authorization", getAuthToken(userB)))
                .andExpect(status().isNotFound());
    }
}
