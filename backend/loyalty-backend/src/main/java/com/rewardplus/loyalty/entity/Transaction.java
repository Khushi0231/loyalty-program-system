package com.rewardplus.loyalty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction entity representing a purchase transaction.
 * Linked to customer and associated loyalty points earned.
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transaction_customer", columnList = "customer_id"),
    @Index(name = "idx_transaction_date", columnList = "transaction_date"),
    @Index(name = "idx_transaction_code", columnList = "transaction_code")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_code", nullable = false, unique = true, length = 50)
    private String transactionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "discount_applied", precision = 10, scale = 2)
    private BigDecimal discountApplied;

    @Column(name = "net_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal netAmount = BigDecimal.ZERO;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 30)
    @Builder.Default
    private TransactionType transactionType = TransactionType.PURCHASE;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TransactionStatus status;

    @Column(name = "store_code", length = 50)
    private String storeCode;

    @Column(name = "store_name", length = 100)
    private String storeName;

    @Column(name = "cashier_code", length = 50)
    private String cashierCode;

    @Column(name = "cashier_name", length = 100)
    private String cashierName;

    @Column(name = "product_category", length = 50)
    private String productCategory;

    @Column(name = "product_details", length = 500)
    private String productDetails;

    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LoyaltyPoints loyaltyPoints;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
        if (transactionCode == null) {
            transactionCode = generateTransactionCode();
        }
        if (status == null) {
            status = TransactionStatus.COMPLETED;
        }
        calculateNetAmount();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private void calculateNetAmount() {
        if (amount != null && discountApplied != null) {
            netAmount = amount.subtract(discountApplied);
        } else if (amount != null) {
            netAmount = amount;
        }
    }

    private String generateTransactionCode() {
        return "TXN" + System.currentTimeMillis();
    }

    /**
     * Transaction type enumeration.
     */
    public enum TransactionType {
        PURCHASE,
        RETURN,
        EXCHANGE,
        PRICE_ADJUSTMENT,
        MANUAL_ADJUSTMENT
    }

    /**
     * Transaction status enumeration.
     */
    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        CANCELLED,
        REFUNDED,
        VOIDED
    }
}

