package com.rewardplus.loyalty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Promotion entity representing marketing campaigns and promotional offers.
 * Can be targeted to specific customer segments based on various criteria.
 */
@Entity
@Table(name = "promotions", indexes = {
    @Index(name = "idx_promotion_status", columnList = "status"),
    @Index(name = "idx_promotion_dates", columnList = "startDate, endDate"),
    @Index(name = "idx_promotion_type", columnList = "promotionType")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String promotionCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private PromotionType promotionType = PromotionType.DISCOUNT;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PromotionStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(precision = 5, scale = 2)
    private Double discountPercentage;

    @Column(precision = 10, scale = 2)
    private Double discountAmount;

    @Column(precision = 10, scale = 2)
    private Double bonusPointsMultiplier;

    private Integer bonusPointsFixed;

    @Column(precision = 10, scale = 2)
    private Double minimumPurchaseAmount;

    @Column(precision = 10, scale = 2)
    private Double maximumDiscount;

    @Column(nullable = false)
    @Builder.Default
    private Integer usageLimit = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer usageCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer usageLimitPerCustomer = 1;

    // Targeting criteria
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Customer.CustomerTier minimumTier;

    @Column
    private Integer minimumAge;

    @Column
    private Integer maximumAge;

    @Column(length = 100)
    private String targetGender;

    @Column(length = 50)
    private String targetOccupation;

    @Column(length = 100)
    private String targetCity;

    @Column(length = 100)
    private String targetState;

    @Column(length = 500)
    private String targetSegmentDescription;

    @Column
    private Integer minimumLifetimeSpend;

    @Column
    private Integer minimumTransactions;

    @Column(length = 100)
    private String targetProductCategory;

    @Column(length = 255)
    private String targetCustomerIds;

    @Column
    private Boolean exclusiveToNewCustomers;

    @Column(length = 500)
    private String termsAndConditions;

    @Column(length = 255)
    private String imageUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "promotion_customers",
        joinColumns = @JoinColumn(name = "promotion_id"),
        inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    @Builder.Default
    private List<Customer> targetCustomers = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 100)
    private String createdBy;

    @Column(length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (promotionCode == null) {
            promotionCode = "PROMO" + System.currentTimeMillis();
        }
        if (status == null) {
            status = PromotionStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if the promotion is currently active and valid.
     */
    public boolean isValid() {
        LocalDate today = LocalDate.now();
        
        if (status != PromotionStatus.ACTIVE) {
            return false;
        }
        if (startDate != null && today.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && today.isAfter(endDate)) {
            return false;
        }
        if (usageLimit > 0 && usageCount >= usageLimit) {
            return false;
        }
        return true;
    }

    /**
     * Increment usage count.
     */
    public void incrementUsage() {
        this.usageCount++;
    }

    /**
     * Promotion type enumeration.
     */
    public enum PromotionType {
        DISCOUNT,
        BONUS_POINTS,
        DOUBLE_POINTS,
        CASHBACK,
        BUY_ONE_GET_ONE,
        FREE_SHIPPING,
        EARLY_ACCESS,
        FLASH_SALE,
        LOYALTY_BOOST,
        TIER_BONUS
    }

    /**
     * Promotion status enumeration.
     */
    public enum PromotionStatus {
        DRAFT,
        SCHEDULED,
        ACTIVE,
        PAUSED,
        EXPIRED,
        CANCELLED
    }
}

