package com.rewardplus.loyalty.repository;

import com.rewardplus.loyalty.entity.Transaction;
import com.rewardplus.loyalty.entity.Transaction.TransactionStatus;
import com.rewardplus.loyalty.entity.Transaction.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Transaction entity operations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find transaction by transaction code.
     */
    Optional<Transaction> findByTransactionCode(String transactionCode);

    /**
     * Check if transaction exists by transaction code.
     */
    boolean existsByTransactionCode(String transactionCode);

    /**
     * Find all transactions for a customer.
     */
    List<Transaction> findByCustomerId(Long customerId);

    /**
     * Find all transactions for a customer with pagination.
     */
    Page<Transaction> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Find transactions by status.
     */
    List<Transaction> findByStatus(TransactionStatus status);

    /**
     * Find transactions by type.
     */
    List<Transaction> findByTransactionType(TransactionType transactionType);

    /**
     * Find transactions within date range.
     */
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Find transactions for customer within date range.
     */
    @Query("SELECT t FROM Transaction t WHERE t.customer.id = :customerId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByCustomerIdAndDateRange(@Param("customerId") Long customerId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    /**
     * Find transactions by store code.
     */
    List<Transaction> findByStoreCode(String storeCode);

    /**
     * Find transactions by store name.
     */
    List<Transaction> findByStoreName(String storeName);

    /**
     * Find transactions by amount range.
     */
    @Query("SELECT t FROM Transaction t WHERE t.amount BETWEEN :minAmount AND :maxAmount")
    List<Transaction> findByAmountRange(@Param("minAmount") BigDecimal minAmount, 
                                         @Param("maxAmount") BigDecimal maxAmount);

    /**
     * Find transactions by cashier.
     */
    List<Transaction> findByCashierCode(String cashierCode);

    /**
     * Find transactions by product category.
     */
    List<Transaction> findByProductCategory(String productCategory);

    /**
     * Count transactions by customer.
     */
    long countByCustomerId(Long customerId);

    /**
     * Count transactions by status.
     */
    long countByStatus(TransactionStatus status);

    /**
     * Sum of transaction amounts for a customer.
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.customer.id = :customerId")
    BigDecimal sumAmountByCustomerId(@Param("customerId") Long customerId);

    /**
     * Sum of net amounts for a customer.
     */
    @Query("SELECT COALESCE(SUM(t.netAmount), 0) FROM Transaction t WHERE t.customer.id = :customerId")
    BigDecimal sumNetAmountByCustomerId(@Param("customerId") Long customerId);

    /**
     * Get recent transactions with limit.
     */
    @Query("SELECT t FROM Transaction t ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentTransactions(Pageable pageable);

    /**
     * Get top spending customers.
     */
    @Query("SELECT t.customer.id, SUM(t.amount) as totalSpent " +
           "FROM Transaction t GROUP BY t.customer.id ORDER BY totalSpent DESC")
    Page<Object[]> findTopSpendingCustomers(Pageable pageable);

    /**
     * Find transactions by customer and status.
     */
    List<Transaction> findByCustomerIdAndStatus(Long customerId, TransactionStatus status);
}

