package com.rewardplus.loyalty.dto;

import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.entity.Promotion;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Promotion entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDTO {

    private Long id;

    @NotBlank(message = "Promotion name is required")
    private String name;

    private String description;

    @NotBlank(message = "Promotion code is required")
    private String promotionCode;

    private Promotion.PromotionType promotionType;
    private Promotion.PromotionStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    @DecimalMin(value = "0.0", message = "Discount percentage cannot be negative")
    private BigDecimal discountPercentage;

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", message = "Bonus points multiplier cannot be negative")
    private BigDecimal bonusPointsMultiplier;

    @Positive(message = "Fixed bonus points must be positive")
    private Integer bonusPointsFixed;

    @DecimalMin(value = "0.0", message = "Minimum purchase amount cannot be negative")
    private BigDecimal minimumPurchaseAmount;

    @DecimalMin(value = "0.0", message = "Maximum discount cannot be negative")
    private BigDecimal maximumDiscount;

    @Positive(message = "Usage limit must be positive")
    private Integer usageLimit;
    private Integer usageCount;
    private Integer usageLimitPerCustomer;

    // Targeting criteria
    private Customer.CustomerTier minimumTier;
    private Integer minimumAge;
    private Integer maximumAge;
    private String targetGender;
    private String targetOccupation;
    private String targetCity;
    private String targetState;
    private String targetSegmentDescription;
    private Integer minimumLifetimeSpend;
    private Integer minimumTransactions;
    private String targetProductCategory;
    private Boolean exclusiveToNewCustomers;

    private String termsAndConditions;
    private String imageUrl;
    private List<Long> targetCustomerIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Computed fields
    private Boolean isValid;
    private Integer remainingUsage;
    private Long targetedCustomerCount;
}

