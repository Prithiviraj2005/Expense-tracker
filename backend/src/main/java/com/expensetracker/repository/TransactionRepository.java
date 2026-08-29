package com.expensetracker.repository;

import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    
    List<Transaction> findByUserId(Long userId);
    
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    BigDecimal sumAmountByUserIdAndType(@Param("userId") Long userId, @Param("type") TransactionType type);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.transactionDate >= :start AND t.transactionDate <= :end")
    BigDecimal sumAmountByUserIdAndTypeAndDateRange(@Param("userId") Long userId, @Param("type") TransactionType type, @Param("start") LocalDate start, @Param("end") LocalDate end);
    
    @Query("SELECT c.name, COALESCE(SUM(t.amount), 0) FROM Transaction t JOIN t.category c WHERE t.user.id = :userId AND t.type = :type GROUP BY c.id, c.name")
    List<Object[]> findCategorySummaryByUserIdAndType(@Param("userId") Long userId, @Param("type") TransactionType type);
    
    @Query("SELECT MONTH(t.transactionDate), SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END) FROM Transaction t WHERE t.user.id = :userId AND YEAR(t.transactionDate) = :year GROUP BY MONTH(t.transactionDate)")
    List<Object[]> findMonthlySummaryByUserIdAndYear(@Param("userId") Long userId, @Param("year") int year);
    
    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, LocalDate start, LocalDate end);
}
