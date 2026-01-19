package com.rewardplus.loyalty.dto;

import com.rewardplus.loyalty.entity.Reward;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Reward entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardDTO {

    private Long id;

    @NotBlank(message = "Reward name is required")
    private String name;

    private String description;

    @NotBlank(message = "Reward code is required")
    private String rewardCode;

    private Reward.RewardType type;
    private Reward.RewardCategory category;

    @NotNull(message = "Points required is required")
    @Positive(message = "Points required must be positive")
    private Long pointsRequired;

    @DecimalMin(value = "0.0", message = "Discount percentage cannot be negative")
    private BigDecimal discountPercentage;

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", message = "Cash value cannot be negative")
    private BigDecimal cashValue;

    private String imageUrl;
    private String termsAndConditions;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    private Integer quantityRedeemed;
    private Integer quantityPerCustomer;

    private Reward.RewardStatus status;
    private LocalDate startDate;
    private LocalDate expiryDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedDate;

    private String redemptionInstructions;
    private String vendorName;
    private String vendorCode;
    private String applicableStores;

    @DecimalMin(value = "0.0", message = "Minimum purchase amount cannot be negative")
    private Integer minimumPurchaseAmount;

    // Computed fields
    private Boolean isAvailable;
    private Integer remainingQuantity;
    private String formattedValue;
}

