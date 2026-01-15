package com.rewardplus.loyalty.dto;

import com.rewardplus.loyalty.entity.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Transaction entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private Long id;

    @NotBlank(message = "Transaction code is required")
    private String transactionCode;

    private Long customerId;
    private String customerName;
    private String customerCode;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    private BigDecimal discountApplied;

    private BigDecimal netAmount;
    private LocalDateTime transactionDate;

    private Transaction.TransactionType transactionType;
    private Transaction.TransactionStatus status;

    private String storeCode;
    private String storeName;
    private String cashierCode;
    private String cashierName;

    private String productCategory;
    private String productDetails;
    private String paymentMethod;
    private String receiptNumber;

    private String notes;

    // Computed fields
    private Long pointsEarned;
    private Boolean eligibleForPoints;
}

